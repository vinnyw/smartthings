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

    definition ( name: "Neo Smart Blinds", namespace: "vinnyw", author: "vinnyw", mcdSync: true, cstHandler: true,
					mnmn: "SmartThings", vid: "generic-shade", ocfDeviceType: "oic.d.blind") {

		capability "Actuator"
		capability "Window Shade"
		//capability "Window Shade Level"
		capability "Window Shade Preset"
	}

	simulator {
		// TODO
	}

	tiles(scale: 2) {
		multiAttributeTile(name:"windowShade", type: "generic", width: 6, height: 4){
			tileAttribute ("device.windowShade", key: "PRIMARY_CONTROL") {
				attributeState "open", label:'${name}', action:"close", icon:"st.shades.shade-open", backgroundColor:"#79b821", nextState:"closing"
				attributeState "closed", label:'${name}', action:"open", icon:"st.shades.shade-closed", backgroundColor:"#ffffff", nextState:"opening"
				attributeState "partially open", label:'Open', action:"close", icon:"st.shades.shade-open", backgroundColor:"#79b821", nextState:"closing"
				attributeState "opening", label:'${name}', action:"pause", icon:"st.shades.shade-opening", backgroundColor:"#79b821", nextState:"partially open"
				attributeState "closing", label:'${name}', action:"pause", icon:"st.shades.shade-closing", backgroundColor:"#ffffff", nextState:"partially open"
				attributeState "unknown", label:'${name}', action:"open", icon:"st.shades.shade-closing", backgroundColor:"#ffffff", nextState:"opening"
			}
			/*tileAttribute ("device.level", key: "SLIDER_CONTROL") {
			   attributeState "level", action:"setLevel"
			}*/
		}
		standardTile("presetPosition", "device.presetPosition", width: 2, height: 2, decoration: "flat") {
			state "default", label: "Preset", action:"presetPosition", icon:"st.Home.home2"
		}

		main "windowShade"
		details(["windowShade","presetPosition"])
	}

	preferences {
		input name: "controllerID", type: "text", title: "Controller ID", description: "\u2630 > Smart Controllers > Controller > ID", required: true
		input name: "controllerIP", type: "text", title: "Controller IP (Local)", description: "\u2630 > Smart Controllers > Controller > IP", required: true
		input name: "blindID", type: "text", title: "Blind code", description: "\u2630 > Your Rooms > Room > Blind > Blind Code", required: true
		input name: "blindDelay", type: "number", title: "Blind timing",
			description: "Blind retraction (seconds)", range: "1..120", displayDuringSetup: false
		input name: "displayDebug", type: "boolean", title: "Debug", defaultValue: false, required: true
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

	if (!controllerID || !controllerIP || !blindCode) {
		writeLog("Setup not fully completed.  Missing required fields.", "ERROR")
		exit
	}

	updated()
	opened()
}

def updated() {
	if (deviceDebug) {
		writeLog("Executing 'updated()'")
		writeLog("updated() settings: $settings", "INFO")
		writeLog("updated() state: $state", "INFO")
	}

	if (!controllerID || !controllerIP || !blindCode) {
		writeLog("Setup not fully completed - Missing required fields.", "ERROR")
		exit
	}

	initialize()
 	sendEvent(name: "supportedWindowShadeCommands", value: ["open", "close", "pause"])    
}

private initialize() {
	if (deviceDebug) {
		writeLog("Executing 'initialize()'")
	}
    //sendEvent(name: "DeviceWatch-Enroll", value: [protocol: "cloud", scheme:"untracked"].encodeAsJson(), displayed: false)
	sendEvent(name: "DeviceWatch-DeviceStatus", value: "online")
	sendEvent(name: "healthStatus", value: "online")
}

def open() {
	if (deviceDebug) {
		writeLog("Executing 'open()'")
	}
	unschedule()
	opening()
	runIn(deviceDelay, "opened", [overwrite: true])
}

def opening() {
	if (deviceDebug) {
		writeLog("Executing 'opening()'")
	}
    attenuate("up")
	sendEvent(name: "windowShade", value: "opening", isStateChange: true)
}

def opened() {
	if (deviceDebug) {
		writeLog("Executing 'opened()'")
	}
	sendEvent(name: "windowShade", value: "open", isStateChange: true)
}

def close() {
	if (deviceDebug) {
		writeLog("Executing 'close()'")
	}
    unschedule()
	closing()
	runIn(deviceDelay, "closed", [overwrite: true])
}

def closing() {
	if (deviceDebug) {
		writeLog("Executing 'closing()'")
    }
    attenuate("dn")
	sendEvent(name: "windowShade", value: "closing", isStateChange: true)
}

def closed() {
	if (deviceDebug) {
		writeLog("Executing 'closed()'")
	}
	sendEvent(name: "windowShade", value: "closed", isStateChange: true)
}

def pause() {
	if (deviceDebug) {
		writeLog("Executing 'pause()'")
	}
    unschedule()
	attenuate("sp")
    paused()
}

def paused() {
	if (deviceDebug) {
		writeLog("Executing 'paused()'")
	}
	sendEvent(name: "windowShade", value: "unknown", isStateChange: true)
}

def presetPosition() {
	if (deviceDebug) {
		writeLog("Executing 'presetPosition()'")
	}
    
    def devicePartialDelay = deviceDelay * 0.75		// 75% of full delay

	unschedule()
	if (device.currentValue("windowShade") == "open") {
		presetPositionCloseing()
		runIn(devicePartialDelay.toInteger(), "presetPositioned", [overwrite: true])
	} else if (device.currentValue("windowShade") == "closed") {
		presetPositionOpening()
		runIn(devicePartialDelay.toInteger(), "presetPositioned", [overwrite: true])
	} else if (device.currentValue("windowShade") == "unknown") {
		presetPositionCloseing()
		runIn(devicePartialDelay.toInteger(), "presetPositioned", [overwrite: true])
	} else {
		attenuate("gp")
		presetPositioned()
	}
}

def presetPositionOpening() {
	if (deviceDebug) {
		writeLog("Executing 'presetPositionedOpening()'")
    }
    attenuate("gp")
	sendEvent(name: "windowShade", value: "opening", isStateChange: true)
}

def presetPositionCloseing() {
	if (deviceDebug) {
		writeLog("Executing 'presetPositionedCloseing()'")
    }
    attenuate("gp")
	sendEvent(name: "windowShade", value: "closing", isStateChange: true)
}

def presetPositioned() {
	if (deviceDebug) {
		writeLog("Executing 'presetPositioned()'")
	}
	sendEvent(name: "windowShade", value: "partially open", isStateChange: true)
}

private attenuate(action) {
	if (deviceDebug) {
		writeLog("Executing 'attenuate($action)'")
	}

	def result = new physicalgraph.device.HubAction(
        method: "GET",
        path: "/neo/v1/transmit",
    	query: [command: "${blindID}-${action}", id: "${controllerID}", hash: "${hash}" ],
        headers: [
            Host: "${controllerIP}:8838",
			Connection: "close",
		],
		null,
        [callback: attenuateCallback]
	)

	try {
		[sendHubCommand(result), "delay 600"]
	} catch (e) {
		writeLog("$e", "ERROR")
	}

	if (deviceDebug) {
		writeLog("\n $result", "INFO")
	}
}

def attenuateCallback(physicalgraph.device.HubResponse hubResponse) {
	if (deviceDebug) {
		writeLog("Executing 'attenuateCallback()'")
		writeLog("${hubResponse}", "INFO")
	}

	switch (hubResponse.status?.toInteger()) {
		case 400:
			writeLog("Response: 400 Bad Request - Command not found or valid", "ERROR")
			sendEvent(name: "windowShade", value: "unknown", isStateChange: true)
			break
		case 401:
			writeLog("Response: 401 Unauthorized - ID not found or valid", "ERROR")
            sendEvent(name: "windowShade", value: "unknown", isStateChange: true)
			break
		case 404:
			writeLog("Response: 404 Not Found - URI not found or valid", "ERROR")
            sendEvent(name: "windowShade", value: "unknown", isStateChange: true)
			break
		case 409:
			writeLog("Response: 409 Conflict - Hash found but already used", "ERROR")
            sendEvent(name: "windowShade", value: "unknown", isStateChange: true)
			break
		case 200:
        	if (deviceDebug) {
				writeLog("Response: 200 OK - Message received and transmitted", "INFO")
			}
			break
		default:
			if (deviceDebug) {
				writeLog("response ${hubResponse.status}", "INFO")
			}
			break
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

private getDeviceDelay() {
	return (settings.blindDelay != null) ? settings.blindDelay.toInteger() : 8
}

private getDeviceDebug() {
	return (settings.displayDebug != null) ? settings.displayDebug.toBoolean() : false
}

private getHash() {
	def currontRandom = new Random().nextInt(9) + 1			// 0-9
	def currentTime = new Date().getTime().toString() 		// ms
	return currontRandom.toString() + currentTime.substring(currentTime.length()-6)	// rnd + last 6 char
}

private getVersion() {
	return "1.0.1"
}

