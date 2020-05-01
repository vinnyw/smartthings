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
					//runLocally: true, minHubCoreVersion: '000.021.00001', executeCommandsLocally: false,
					mnmn: "SmartThings", vid: "generic-switch", ocfDeviceType: "oic.d.switch") {

		capability "Actuator"
		capability "Switch"
		capability "Contact Sensor"
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
        input name: "autoReset", type: "boolean", title: "Auto reset", defaultValue: false, required: true
        input name: "displayDebug", type: "boolean", title: "Debug", defaultValue: false, required: true
	}

}

def parse(description) {
	if (displayDebug?.toBoolean() ?: false) {
		writeLog("Parsing '${description}'")
	}
	// TODO
}

def installed() {
	if (displayDebug?.toBoolean() ?: false) {
		writeLog("Executing 'installed()'")
		writeState("installed()")
	}
    off()
    initialize()
}

def updated() {
	if (displayDebug?.toBoolean() ?: false) {
		writeLog("Executing 'updated()'")
		writeState("updated()")
	}
	initialize()
}

private initialize() {
	if (displayDebug?.toBoolean() ?: false) {
		writeLog("Executing 'initialize()'")
	}
    //sendEvent(name: "DeviceWatch-Enroll", value: [protocol: "cloud", scheme:"untracked"].encodeAsJson(), displayed: false)
	sendEvent(name: "DeviceWatch-DeviceStatus", value: "online")
	sendEvent(name: "healthStatus", value: "online")
}

def on() {
	if (displayDebug?.toBoolean() ?: false) {
        writeLog("Executing 'on()'")
	}

	sendEvent(name: "switch", value: "on")
	sendEvent(name: "contact", value: "close", isStateChange: true, displayed: false)

	if (autoReset?.toBoolean() ?: false) {
		runIn(1, "off", [overwrite: true])
	}
}

def off() {
	if (displayDebug?.toBoolean() ?: false) {
		writeLog("Executing 'off()'")
	}

    unschedule()

	sendEvent(name: "switch", value: "off")

	if (!autoReset?.toBoolean() ?: false) {
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

private writeState(message) {
	log.debug ("${device} [v$version]: ${message} settings ${settings}")
	log.debug ("${device} [v$version]: ${message} state ${state}")
}

private getVersion() {
	return "1.1.29"
}
