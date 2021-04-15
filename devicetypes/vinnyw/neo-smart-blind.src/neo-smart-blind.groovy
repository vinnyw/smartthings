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
import groovy.json.JsonOutput

metadata {

	definition ( name: "Neo Smart Blind", namespace: "vinnyw", author: "vinnyw", 
		mnmn: "SmartThings", vid: "generic-shade", ocfDeviceType: "oic.d.blind") {

		capability "Window Shade"
		capability "Window Shade Level"
		capability "Window Shade Preset"
		capability "Health Check"

		command "open"
		command "close"
		command "pause"
		command "presetPosition"

	}

	simulator {
		// TODO
	}

	tiles(scale: 2) {
		multiAttributeTile(name:"windowShade", type: "generic", width: 6, height: 4, canChangeIcon: true) {
			tileAttribute ("device.windowShade", key: "PRIMARY_CONTROL") {
				attributeState "open", label:'${name}', action:"close", icon:"st.shades.shade-open", backgroundColor:"#79b821", nextState:"closing"
				attributeState "closed", label:'${name}', action:"open", icon:"st.shades.shade-closed", backgroundColor:"#ffffff", nextState:"opening"
				attributeState "partially open", label:'${name}', action:"close", icon:"st.shades.shade-open", backgroundColor:"#79b821", nextState:"closing"
				attributeState "opening", label:'${name}', action:"stop", icon:"st.shades.shade-opening", backgroundColor:"#79b821", nextState:"partially open"
				attributeState "closing", label:'${name}', action:"stop", icon:"st.shades.shade-closing", backgroundColor:"#ffffff", nextState:"partially open"
				attributeState "unknown", label:'${name}', action:"close", icon:"st.shades.shade-open", backgroundColor:"#79b821", nextState:"closing"
			}
			//tileAttribute ("device.level", key: "SLIDER_CONTROL") {
			//		attributeState "level", action:"setLevel"
			//}
		}

		standardTile("presetPosition", "device.presetPosition", width: 2, height: 2, decoration: "flat") {
			state "default", label: "Preset", action:"presetPosition", icon:"st.Home.home2"
		}

		main(["windowShade"])
        details(["windowShade", "presetPosition"])
	}

	preferences {
		input name: "controllerID", type: "text", title: "Controller ID", description: "\u2630 > Smart Controllers > Controller > ID", required: true
		input name: "controllerIP", type: "text", title: "Controller IP (Local)", description: "\u2630 > Smart Controllers > Controller > IP", required: true
		input name: "blindID", type: "text", title: "Blind code", description: "\u2630 > Your Rooms > Room > Blind > Blind Code", required: true
        input name: "blindDelay", type: "number", title: "Blind timing", description: "Time in seconds (Default: 15)", range: "1..120", displayDuringSetup: false
        input name: "blindPreset", type: "number", title: "Preset position ", description: "Approximate percentage (Default: 50)", range: "1..99", displayDuringSetup: false
        input name: "deviceEvent", type: "boolean", title: "Ignore device state?", defaultValue: false, required: true
		input name: "deviceDebug", type: "boolean", title: "Show debug log?", defaultValue: false, required: true
		input type: "paragraph", element: "paragraph", title: "Neo Smart Blind", description: "${version}", displayDuringSetup: false
	}
}

def installed() {
	if (deviceDebug) {
		writeLog("installed()")
		writeLog("settings: $settings", "INFO")
		writeLog("state: $state", "INFO")
	}

	//if (!controllerID || !controllerIP || !blindCode) {
	//	writeLog("Setup not fully completed.  Missing required fields.", "ERROR")
	//	return
	//}

	initialize()
	updated()
	opened()
}

private initialize() {
	if (deviceDebug) {
		writeLog("initialize()")
		writeLog("settings: $settings", "INFO")
		writeLog("state: $state", "INFO")
	}

	sendEvent(name: "DeviceWatch-Enroll", value: [protocol: "cloud", scheme:"untracked"].encodeAsJson(), displayed: false)
	sendEvent(name: "DeviceWatch-DeviceStatus", value: "online", displayed: false)
	sendEvent(name: "healthStatus", value: "online", displayed: false)

	sendEvent(name: "supportedWindowShadeCommands", value: JsonOutput.toJson(["open", "close", "pause"]), displayed: false)
}

def updated() {
	if (deviceDebug) {
		writeLog("updated()")
		writeLog("settings: $settings", "INFO")
		writeLog("state: $state", "INFO")
	}

	//if (!controllerID || !controllerIP || !blindCode) {
	//	writeLog("Setup not fully completed - Missing required fields.", "ERROR")
	//	return
	//}

	// clean up
   	state.remove("level")
}

def open() {
	if (deviceDebug) {
		writeLog("open()")
		writeLog("settings: $settings", "INFO")
		writeLog("state: $state", "INFO")
	}

	def shadeState = device.currentState("windowShade")?.value

	if ((shadeState.equalsIgnoreCase("open")) && !deviceEvent) {
		if (deviceDebug) {
			writeLog("no action required.")
		}
		return
	}

	unschedule()
    if (shadeState.equalsIgnoreCase("opening") || shadeState.equalsIgnoreCase("closing")) {
		pause()
		return
	}

	opening()
	runIn(timeToLevel(0), "opened", [overwrite: true])
}

def opening(direction = "up") {
	if (deviceDebug) {
		writeLog("opening($direction)")
	}

	attenuate(direction)
	sendEvent(name: "windowShade", value: "opening", isStateChange: true, displayed: false)
}

def opened() {
	if (deviceDebug) {
		writeLog("opened()")
	}

	sendEvent(name: "windowShade", value: "open", isStateChange: true)
	sendEvent(name: "shadeLevel", value: 0, unit: "%", isStateChange: false, displayed: false)
}

def close() {
	if (deviceDebug) {
		writeLog("close()")
		writeLog("settings: $settings", "INFO")
		writeLog("state: $state", "INFO")
	}

	def shadeState = device.currentState("windowShade")?.value

	if ((shadeState.equalsIgnoreCase("closed")) && !deviceEvent) {
		if (deviceDebug) {
			writeLog("no action required.")
		}
		return
	}

	unschedule()
    if (shadeState.equalsIgnoreCase("opening") || shadeState.equalsIgnoreCase("closing")) {
		pause()
		return
	}

	closing()
	runIn(timeToLevel(100), "closed", [overwrite: true])
}

def closing(direction = "dn") {
	if (deviceDebug) {
		writeLog("closing($direction)")
	}

	attenuate(direction)
	sendEvent(name: "windowShade", value: "closing", isStateChange: false, displayed: false)
}

def closed() {
	if (deviceDebug) {
		writeLog("closed()")
	}

	sendEvent(name: "windowShade", value: "closed", isStateChange: true)
	sendEvent(name: "shadeLevel", value: 100, unit: "%", isStateChange: false, displayed: false)
}

def pause() {
	if (deviceDebug) {
		writeLog("pause()")
		writeLog("settings: $settings", "INFO")
		writeLog("state: $state", "INFO")
	}

	def shadeState = device.currentState("windowShade")?.value

	if ((shadeState.equalsIgnoreCase("unknown")) && !deviceEvent) {
		if (deviceDebug) {
			writeLog("no action required.")
		}
		return
	}

	unschedule()
    if (shadeState.equalsIgnoreCase("opening") || shadeState.equalsIgnoreCase("closing")) {
    	attenuate("sp")
		sendEvent(name: "windowShade", value: "unknown", isStateChange: true)
	} else {
    	sendEvent(name: "windowShade", value: "${shadeState}", isStateChange: false, displayed: false)
    }
}

def presetPosition() {
	if (deviceDebug) {
		writeLog("presetPosition()")
 		writeLog("settings: $settings", "INFO")
		writeLog("state: $state", "INFO")
	}

    def shadeState = device.currentState("windowShade")?.value
	def shadeLevel = device.currentState("shadeLevel")?.value.toInteger()

	if ((shadeLevel == blindPreset) && !deviceEvent) {
		if (deviceDebug) {
			writeLog("no action required.")
		}
		return
	}

	unschedule()
    if (shadeState.equalsIgnoreCase("opening") || shadeState.equalsIgnoreCase("closing")) {
		sendEvent(name: "windowShade", value: "unknown", isStateChange: true)
	} else {
    	sendEvent(name: "windowShade", value: "${shadeState}", isStateChange: false, displayed: false)
    }

	if (shadeLevel == 0) {
		closing("gp")
		runIn(timeToLevel(blindPreset), "presetPositioned", [overwrite: true])
	} else if (shadeLevel == 100) {
		opening("gp")
		runIn(timeToLevel(blindPreset), "presetPositioned", [overwrite: true])
	} else {
		attenuate("gp")
        presetPositioned()
	}
}

def presetPositioned() {
	if (deviceDebug) {
		writeLog("presetPositioned()")
	}

	sendEvent(name: "windowShade", value: "partially open", isStateChange: true)
	sendEvent(name: "shadeLevel", value: blindPreset.toInteger(), unit: "%", isStateChange: false, displayed: false)
}

private attenuate(action) {
	if (deviceDebug) {
		writeLog("attenuate($action)")
	}

	def result = new physicalgraph.device.HubAction (
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
    	delayBetween([sendHubCommand(result)], 750)
	} catch (e) {
		writeLog("$e", "ERROR")
	}

	//if (deviceDebug) {
	//	writeLog("\n $result", "INFO")
	//}
}

def attenuateCallback(physicalgraph.device.HubResponse hubResponse) {
	if (deviceDebug) {
		writeLog("attenuateCallback()")
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

private timeToLevel(targetLevel) {
	if (deviceDebug) {
		writeLog("timeToLevel(${targetLevel})")
	}

    def currentLevel = device.currentState("shadeLevel")?.value.toFloat()
    def timeDelay = blindDelay.toInteger()

	def percentTime = timeDelay / 100
    def levelDiff = currentLevel - targetLevel
    def runTime = levelDiff > 0 ? (percentTime * levelDiff) : (percentTime * -levelDiff)

	if (deviceDebug) {
		writeLog("runtime: ${runTime}s" , "INFO")
	}
	return runTime
}

private writeLog(message, type = "DEBUG") {
	message = "${device} [v$version] ${message ?: ''}"
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

private getBlindDelay() {
	return (settings.blindDelay != null) ? settings.blindDelay.toInteger() : 15
}

private getBlindPreset() {
	return (settings.blindPreset != null) ? settings.blindPreset.toInteger() : 50
}

private getDeviceEvent() {
	return (settings.deviceEvent != null) ? settings.deviceEvent.toBoolean() : false
}

private getDeviceDebug() {
	return (settings.deviceDebug != null) ? settings.deviceDebug.toBoolean() : false
}

private getHash() {
	def currontRandom = new Random().nextInt(9) + 1			// 0-9
	def currentTime = new Date().getTime().toString() 		// ms
	return currontRandom.toString() + currentTime.substring(currentTime.length()-6)	// rnd + last 6 char
}

private getVersion() {
	return "1.6.1"
}