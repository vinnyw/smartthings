/**
 *  zzz Window Shade
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
	definition (name: "zzz Window Shade", namespace: "vinnyw", author: "vinnyw", mnmn: "SmartThings", cstHandler: true, vid: "generic-shade", ocfDeviceType: "oic.d.blind" ) {
		capability "Actuator"
		capability "Window Shade"
		//capability "Window Shade Level"
		capability "Window Shade Preset"
	}

	simulator {
		// TODO: define status and reply messages here
	}

	preferences {
		section {
			input("actionDelay", "number",
				title: "Action Delay\n\nAn emulation for how long it takes the window shade to perform the requested action.",
				description: "In seconds (1-120; default if empty: 5 sec)",
				range: "1..120", displayDuringSetup: false)
		}
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
}

// parse events into attributes
def parse(String description) {
	log.debug "Parsing '${description}'"
	// TODO: handle 'windowShade' attribute
	// TODO: handle 'supportedWindowShadeCommands' attribute
	// TODO: handle 'shadeLevel' attribute

}

def installed() {
	log.debug "installed()"

	updated()
    opened()
}

def updated() {
	log.debug "updated()"

    sendEvent(name: "supportedWindowShadeCommands", value: ["open", "close", "pause"])    
}

def open() {
	log.debug "open()"
	opening()
	runIn(5, "opened")
}

def opening() {
	log.debug "windowShade: opening"
	sendEvent(name: "windowShade", value: "opening", isStateChange: true)
}

def opened() {
	log.debug "windowShade: open"
	sendEvent(name: "windowShade", value: "open", isStateChange: true)
}

def close() {
	log.debug "close()"
	closing()
	runIn(5, "closed")
}

def closing() {
	log.debug "windowShade: closing"
	sendEvent(name: "windowShade", value: "closing", isStateChange: true)
}

def closed() {
	log.debug "windowShade: closed"
	sendEvent(name: "windowShade", value: "closed", isStateChange: true)
}


def presetPosition() {
	log.debug "presetPosition()"
	if (device.currentValue("windowShade") == "open") {
		closePartially()
	} else if (device.currentValue("windowShade") == "closed") {
		opening()
		runIn(shadeActionDelay, "partiallyOpen")
   	}
}

def openPartially() {
	log.debug "windowShade: partially open"
	sendEvent(name: "windowShade", value: "partially open", isStateChange: true)
}


def pause() {
	log.debug "pause()"
	partiallyOpen()
}

def partiallyOpen() {
	log.debug "windowShade: partially open"
	sendEvent(name: "windowShade", value: "partially open", isStateChange: true)
}




def unknown() {
	// TODO: Add some "fuzzing" logic so that this gets hit every now and then?
	log.debug "windowShade: unknown"
	sendEvent(name: "windowShade", value: "unknown", isStateChange: true)
}


