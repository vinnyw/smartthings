/**
 *  Virtual Presence Sensor
 *
 *  Copyright 2020 vinnyw
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 */
metadata {
	definition (name: "Virtual Presence", namespace: "vinnyw", author: "vinnyw", mcdSync: true, cstHandler: true,
		mnmn: "SmartThings", vid: "generic-arrival-2", ocfDeviceType: "x.com.st.d.sensor.presence" ) {

		capability "Actuator"
		capability "Switch"        
		capability "Presence Sensor"
        capability "Health Check"

		command "arrive"
		command "depart"

	}

	simulator {
		// TODO
	}

	tiles(scale: 2) {

		// UI tile definitions
		standardTile("button", "device.switch", width: 2, height: 2, canChangeIcon: false,  canChangeBackground: true) {
			state "off", label: 'Away', action: "switch.on", icon: "st.switches.switch.off", backgroundColor: "#00A0DC"
			state "on", label: 'Present', action: "switch.off", icon: "st.switches.switch.on", backgroundColor: "#FFFFFF"
		}

		standardTile("presence", "device.presence", width: 1, height: 1, canChangeBackground: true) {
			state("present", Icon:"st.presence.tile.mobile-present", backgroundColor:"#53a7c0")
			state("not present", Icon:"st.presence.tile.mobile-not-present", backgroundColor:"#ffffff")
		}
        
 		main (["button"])
		details(["button","presense"])
 
    }

	preferences {
		input name: "deviceReset", type: "boolean", title: "Auto reset switch?", defaultValue: false, required: true
		input name: "deviceEvent", type: "boolean", title: "Always raise event?", defaultValue: false, required: true
		input name: "deviceDebug", type: "boolean", title: "Show debug log?", defaultValue: false, required: true
		input type: "paragraph", element: "paragraph", title: "Virtual Presense", description: "${version}", displayDuringSetup: false
	}

}

// parse events into attributes
def parse(String description) {
	log.debug "Parsing '${description}'"
	// TODO: handle 'presence' attribute

}

def installed() {
	off()
}



def arrived() {
	on()
}


def departed() {
    off()
}

def on() {
	sendEvent(name: "switch", value: "on")
    sendEvent(name: "presence", value: "present")

}

def off() {
	sendEvent(name: "switch", value: "off")
    sendEvent(name: "presence", value: "not present")
}



private getState(String value) {
	switch(value) {
		case "present": return "arrived"
		case "not present": return "left"
		case "occupied": return "inside"
		case "unoccupied": return "away"
		default: return value
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

private getDeviceEvent() {
	return (settings.deviceEvent != null) ? settings.deviceEvent.toBoolean() : false
}

private getDeviceDebug() {
	return (settings.deviceDebug != null) ? settings.deviceDebug.toBoolean() : false
}

private getVersion() {
	return "0.0.1"
}
