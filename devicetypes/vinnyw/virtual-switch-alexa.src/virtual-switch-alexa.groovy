/**
 *  Copyright 2015 SmartThings
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
 **/
metadata {

	definition ( name: "Virtual Switch (Alexa)", namespace: "vinnyw", author: "vinnyw", mcdSync: true,
		mnmn: "SmartThings", vid: "generic-switch", ocfDeviceType: "oic.d.switch") {

		capability "Actuator"
		capability "Switch"
		capability "Contact Sensor"

		command "on"
		command "off"
	}

	simulator {
		// TODO
	}

	tiles {
		standardTile("switch", "device.switch", decoration: "flat", width: 3, height: 2, canChangeIcon: true, canChangeBackground: true) {
			state "off", label: '${currentValue}', action: "switch.on", icon: "st.switches.switch.off", backgroundColor: "#ffffff", nextState:"turningOn"
			state "on", label: '${currentValue}', action: "switch.off", icon: "st.switches.switch.on", backgroundColor: "#00A0DC", nextState:"turningOff"
			state "turningOn", label:'Turning On', action:"switch.off", icon:"st.switches.switch.on", backgroundColor:"#00A0DC", nextState:"turningOff"
			state "turningOff", label:'Turning Off', action:"switch.on", icon:"st.switches.switch.off", backgroundColor:"#ffffff", nextState:"turningOn"
		}

		main "switch"
		details(["switch"])
	}

	preferences {
		input name: "deviceReset", type: "boolean", title: "Auto reset switch?", defaultValue: false, required: true
		input name: "raiseEvent", type: "boolean", title: "Always raise event?", defaultValue: false, required: true
		input name: "deviceDebug", type: "boolean", title: "Show debug log?", defaultValue: false, required: true
		input type: "paragraph", element: "paragraph", title: "Virtual Switch (Alexa)", description: "${version}", displayDuringSetup: false
	}

}

def parse(description) {
	if (deviceDebug) {
		writeLog("Parsing '${description}'")
	}
	// TODO
}

def installed() {
	if (deviceDebug) {
		writeLog("Executing 'installed()'")
		writeLog("installed() settings: $settings", "INFO")
		writeLog("installed() state: $state", "INFO")
	}

	off()
	initialize()
}

def updated() {
	if (deviceDebug) {
		writeLog("Executing 'updated()'")
		writeLog("updated() settings: $settings", "INFO")
		writeLog("updated() state: $state", "INFO")
	}

	initialize()
}

private initialize() {
	if (deviceDebug) {
		writeLog("Executing 'initialize()'")
	}

	sendEvent(name: "DeviceWatch-Enroll", value: [protocol: "cloud", scheme:"untracked"].encodeAsJson())
	sendEvent(name: "DeviceWatch-DeviceStatus", value: "online")
	sendEvent(name: "healthStatus", value: "online")
}

def on() {
	if (deviceDebug) {
		writeLog("Executing 'on()'")
	}

	if ((device.currentValue("switch") == "on") && !raiseEvent) {
		if (deviceDebug) {
			writeLog("no action required.  state is already " + device.currentValue("switch"))
		}
		return
	}

	sendEvent(name: "switch", value: "on", isStateChange: true)
	sendEvent(name: "contact", value: "closed", isStateChange: true, displayed: false)

	if (deviceReset) {
		runIn(1, "off", [overwrite: true])
	}
}

def off() {
	if (deviceDebug) {
		writeLog("Executing 'off()'")
	}

	if ((device.currentValue("switch") == "off") && !raiseEvent) {
		if (deviceDebug) {
			writeLog("no action required.  state is already " + device.currentValue("switch"))
		}
		return
	}

	unschedule()
	sendEvent(name: "switch", value: "off", isStateChange: true)

	if (deviceReset) {
		sendEvent(name: "contact", value: "", isStateChange: false, displayed: false)
	} else {
		sendEvent(name: "contact", value: "open", isStateChange: true, displayed: false)
	}
}

private writeLog(message, type = "DEBUG") {
	message = "${device} [v$version]: ${message ?: ''}"
	switch (type?.toUpperCase()) {
		case "TRACE":
		log.trace "${message}"
		break
		case "DEBUG":
			log.debug "${message}"
			break
		case "INFO":
			log.info "${message}"
			break
		case "WARN":
			log.warn "${message}"
			break
		case "ERROR":
			log.error "${message}"
			break
		default:
			log.debug "${message}"
	}
}

private getDeviceReset() {
	return (settings.deviceReset != null) ? settings.deviceReset.toBoolean() : false
}

private getRaiseEvent() {
	return (settings.raiseEvent != null) ? settings.raiseEvent.toBoolean() : false
}

private getDeviceDebug() {
	return (settings.deviceDebug != null) ? settings.deviceDebug.toBoolean() : false
}

private getVersion() {
	return "1.1.42"
}

