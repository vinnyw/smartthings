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
 */
metadata {

    definition (name: "zzz sim button 2", namespace: "vinnyw", author: "vinnyw", runLocally: false, mnmn: "SmartThings", vid: "generic-switch") {
		capability "Switch"
		capability "Sensor"
		capability "Contact Sensor"
    }

	tiles {
		standardTile("switch", "device.switch", width: 2, height: 2, canChangeIcon: true) {
			state "off", label: '${currentValue}', action: "switch.on", icon: "st.switches.switch.off", backgroundColor: "#ffffff"
			state "on", label: '${currentValue}', action: "switch.off", icon: "st.switches.switch.on", backgroundColor: "#00A0DC"
		}

		main "switch"
		details(["switch"])
    }

	preferences {
		section {
			input(title: "======= Boolean Types Title =======",
					description: "Boolean Types Description",
					displayDuringSetup: false,
					type: "paragraph",
					element: "paragraph")
			input("displayDebug", "boolean",
					title: "Debug",
					defaultValue: "false",
					required: true)
		}
	}

}


def parse(description) {
}

def installed() {
    log.trace "Executing 'installed'"
    off()
    initialize()
}

def updated() {
    log.trace "Executing 'updated'"
    initialize()
}

private setDeviceHealth(String healthState) {
    log.debug("healthStatus: ${device.currentValue('healthStatus')}; DeviceWatch-DeviceStatus: ${device.currentValue('DeviceWatch-DeviceStatus')}")
    List validHealthStates = ["online", "offline"]
    healthState = validHealthStates.contains(healthState) ? healthState : device.currentValue("healthStatus")
    // set the healthState
    sendEvent(name: "DeviceWatch-DeviceStatus", value: healthState)
    sendEvent(name: "healthStatus", value: healthState)
}

private initialize() {
    log.trace "Executing 'initialize'"
    //sendEvent(name: "DeviceWatch-Enroll", value: [protocol: "cloud", scheme:"untracked"].encodeAsJson(), displayed: false)
	sendEvent(name: "DeviceWatch-DeviceStatus", value: "online")
	sendEvent(name: "healthStatus", value: "online")

}

def on() {
	if (displayDebug ? true : false) {
        writeLog("Executing 'on()'")
	}
	sendEvent(name: "switch", value: "on")
	sendEvent(name: "contact", value: "close", isStateChange: true, displayed: false)
}

def off() {
	if (displayDebug ? true : false) {
        writeLog("Executing 'off()'")
	}
	sendEvent(name: "switch", value: "off")
	sendEvent(name: "contact", value: "open", isStateChange: true, displayed: false)
}

private writeLog(message) {
	log.debug ("${device} [v$version]: ${message}")
}

private writeState(message) {
	log.debug ("${device} [v$version]: ${message} settings ${settings}")
	log.debug ("${device} [v$version]: ${message} state ${state}")
}

private getVersion() {
	return "1.0.20"
}

