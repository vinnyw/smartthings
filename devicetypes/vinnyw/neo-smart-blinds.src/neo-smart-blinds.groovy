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
			description: "Seconds for complete blind retraction (default: 5)", range: "1..120", displayDuringSetup: false
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
	updated()
	opened()
}

def updated() {
	if (deviceDebug) {
		writeLog("Executing 'updated()'")
		writeLog("updated() settings: $settings", "INFO")
		writeLog("updated() state: $state", "INFO")
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

/** OPEN **/
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

/** CLOSE **/
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

/** PAUSE/STOP **/
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
            HOST: "${controllerIP}:8838",
			CONNECTION: "close",
		],
	)
	sendHubCommand(result)
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
	return (settings.blindDelay != null) ? settings.blindDelay.toInteger() : 5
}

private getDeviceDebug() {
	return (settings.displayDebug != null) ? settings.displayDebug.toBoolean() : false
}

private getHash() {
	def currentTime = new Date().getTime().toString() 		// ms
	return currentTime.substring(currentTime.length()-7)	// last 7 char
}

private getVersion() {
	return "0.0.1"
}

