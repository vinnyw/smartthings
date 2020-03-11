import groovy.json.JsonOutput

/**
 *  Z-Wave Multi Button
 *
 *  Copyright 2019 SmartThings
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */

metadata {
	definition (name: "TechniSat Szenenschalter", namespace: "vinnyw", author: "vinnyw", mcdSync: true, ocfDeviceType: "x.com.st.d.remotecontroller") {
		capability "Button"
		capability "Battery"
		capability "Sensor"
		capability "Health Check"
		capability "Configuration"

		// While adding new device to this DTH, remember to update method getProdNumberOfButtons()
		fingerprint mfr: "0208", prod: "0200", model: "000B", deviceJoinName: "TechniSat Szenenschalter", mnmn: "SmartThings", vid: "generic-4-button" //US

	}

	tiles(scale: 2) {
		multiAttributeTile(name: "button", type: "generic", width: 6, height: 4, canChangeIcon: true) {
			tileAttribute("device.button", key: "PRIMARY_CONTROL") {
				attributeState "default", label: ' ', icon: "st.unknown.zwave.remote-controller", backgroundColor: "#ffffff"
			}
		}
		valueTile("battery", "device.battery", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "battery", label:'${currentValue}% battery', unit:""
		}

		main "button"
		details(["button", "battery"])
	}
}

def installed() {
	sendEvent(name: "button", value: "pushed", isStateChange: true, displayed: false)
	sendEvent(name: "supportedButtonValues", value: supportedButtonValues.encodeAsJSON(), displayed: false)
	initialize()
}

def updated() {
	runIn(2, "initialize", [overwrite: true])
}


def initialize() {
	def numberOfButtons = prodNumberOfButtons[zwaveInfo.prod]
	sendEvent(name: "numberOfButtons", value: numberOfButtons, displayed: false)
    sendEvent(name: "checkInterval", value: 8 * 60 * 60 + 10 * 60, displayed: false, data: [protocol: "zwave", hubHardwareId: device.hub.hardwareID])
    if(!childDevices) {
        addChildButtons(numberOfButtons)
	}
	if(childDevices) {
        def event
        for(def endpoint : 1..prodNumberOfButtons[zwaveInfo.prod]) {
            event = createEvent(name: "button", value: "pushed", isStateChange: true)
            sendEventToChild(endpoint, event)
        }   
    }
	response([
			secure(zwave.batteryV1.batteryGet()),
			"delay 2000",
			secure(zwave.wakeUpV1.wakeUpNoMoreInformation())
	])
}

def configure() {
	def cmds = []
	cmds
}

def parse(String description) {
	def result = []
	if (description.startsWith("Err")) {
		result = createEvent(descriptionText:description, isStateChange:true)
	} else {
		def cmd = zwave.parse(description, [0x84: 1])
		if (cmd) {
			result += zwaveEvent(cmd)
		}
	}
	log.debug "Parse returned: ${result}"
	result
}

def zwaveEvent(physicalgraph.zwave.commands.securityv1.SecurityMessageEncapsulation cmd) {
	def encapsulatedCommand = cmd.encapsulatedCommand([0x84: 1])
	if (encapsulatedCommand) {
		zwaveEvent(encapsulatedCommand)
	} else {
		log.warn "Unable to extract encapsulated cmd from $cmd"
		createEvent(descriptionText: cmd.toString())
	}
}

def zwaveEvent(physicalgraph.zwave.commands.sceneactivationv1.SceneActivationSet cmd) {
	// Below handler was tested with Aoetec KeyFob and probably will work only with it
	def value = cmd.sceneId % 2 ? "pushed" : "held"
	def childId = (int)(cmd.sceneId / 2) + (cmd.sceneId % 2)
	def description = "Button no. ${childId} was ${value}"
	def event = createEvent(name: "button", value: value, descriptionText: description, data: [buttonNumber: childId], isStateChange: true)
	sendEventToChild(childId, event)
	return event
}

def zwaveEvent(physicalgraph.zwave.commands.centralscenev1.CentralSceneNotification cmd) {
	def value = eventsMap[(int) cmd.keyAttributes]
	def description = "Button no. ${cmd.sceneNumber} was ${value}"
	def event = createEvent(name: "button", value: value, descriptionText: description, data: [buttonNumber: cmd.sceneNumber], isStateChange: true)
	sendEventToChild(cmd.sceneNumber, event)
	return event
}

def sendEventToChild(buttonNumber, event) {
	String childDni = "${device.deviceNetworkId}:$buttonNumber"
	def child = childDevices.find { it.deviceNetworkId == childDni }
	child?.sendEvent(event)
}

def zwaveEvent(physicalgraph.zwave.commands.wakeupv1.WakeUpNotification cmd) {
	def results = []
	results += createEvent(descriptionText: "$device.displayName woke up", isStateChange: false)
	results += response([
			secure(zwave.batteryV1.batteryGet()),
			"delay 2000",
			secure(zwave.wakeUpV1.wakeUpNoMoreInformation())
	])
	results
}

def zwaveEvent(physicalgraph.zwave.commands.batteryv1.BatteryReport cmd) {
	def map = [ name: "battery", unit: "%", isStateChange: true ]
	state.lastbatt = now()
	if (cmd.batteryLevel == 0xFF) {
		map.value = 1
		map.descriptionText = "$device.displayName battery is low!"
	} else {
		map.value = cmd.batteryLevel
	}
	createEvent(map)
}

def zwaveEvent(physicalgraph.zwave.Command cmd) {
	log.warn "Unhandled command: ${cmd}"
}

private secure(cmd) {
	if(zwaveInfo.zw.endsWith("s")) {
		zwave.securityV1.securityMessageEncapsulation().encapsulate(cmd).format()
	} else {
		cmd.format()
	}
}

private addChildButtons(numberOfButtons) {
	for(def endpoint : 1..numberOfButtons) {
		try {
			String childDni = "${device.deviceNetworkId}:$endpoint"
			def componentLabel = (device.displayName.endsWith(' 1') ? device.displayName[0..-2] : (device.displayName + " ")) + "${endpoint}"
			def child = addChildDevice("Child Button", childDni, device.getHub().getId(), [
					completedSetup: true,
					label         : componentLabel,
					isComponent   : true,
					componentName : "button$endpoint",
					componentLabel: "Button $endpoint"
			])
			child.sendEvent(name: "supportedButtonValues", value: supportedButtonValues.encodeAsJSON(), displayed: false)
		} catch(Exception e) {
			log.debug "Exception: ${e}"
		}
	}
}

private getEventsMap() {[
		0: "pushed",
		1: "held",
		2: "down_hold",
		3: "double",
		4: "pushed_3x"
]}

private getProdNumberOfButtons() {[
		"0200" : 4
]}

private getSupportedButtonValues() {
	def values = ["pushed", "held"]
	return values
}


