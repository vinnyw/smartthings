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
	definition (name: "Virtual Presence", namespace: "vinnyw", author: "vinnyw", cstHandler: true, 
    	mnmn: "SmartThings", vid: "generic-arrival-2", ocfDeviceType: "x.com.st.d.sensor.presence" ) {
		capability "Switch"        
		capability "Presence Sensor"

		command "arrive"
		command "depart"

	}

	simulator {
		// TODO: define status and reply messages here
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




