/**
 *  Copyright 2017 SmartThings
 *
 *  Provides a virtual switch.
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
    definition (name: "Simulated Contact Switch", namespace: "vinnyw", author: "Vinny Wadding", cstHandler: true) {
    	//runLocally: true, minHubCoreVersion: '000.021.00001', executeCommandsLocally: true
        //capability "Actuator"
        //capability "Sensor"
        //capability "Contact Sensor"	
        capability "Switch"

		command "on"
		command "off"

 	}

    preferences {
    }

    tiles(scale: 2) {

     	standardTile("switch", "device.switch", width: 2, height: 2, decoration: "flat", canChangeBackground: true, canChangeIcon: true) {
            state("off", label: '${currentValue}', action: "switch.on", icon: "st.switches.switch.off", backgroundColor: "#ffffff", nextState: "on")
            state("on", label: '${currentValue}', action: "switch.off", icon: "st.switches.switch.on", backgroundColor: "#00A0DC", nextState: "off")
        }

    	// tile with name 'switch' appears in the Things view
        main(["switch"])

     	// switch tile is top left, then otherTile, then all flowing left-to-right, top-to-bottom:
        details(["switch"])

    }
}

def parse(String description) {
	def pair = description.split(":")
	createEvent(name: pair[0].trim(), value: pair[1].trim())
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

private initialize() {
    log.trace "Executing 'initialize'"
}

def on() {
	log.trace "on()"
    sendEvent(name: "switch", value: "on", isStateChange: true)
    sendEvent(name: "contact", value: "open", isStateChange: true)
}

def off() {
	log.trace "off()"
  	sendEvent(name: "switch", value: "off", isStateChange: true)
    sendEvent(name: "contact", value: "closed", isStateChange: true)
}

