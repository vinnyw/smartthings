/**
 *  Copyright 2014 SmartThings
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
	// Automatically generated. Make future change here.
	definition (name: "Simulated Contact Switch", namespace: "vinnyw", author: "Vinny Wadding") {
        capability "Switch"
      	capability "Contact Sensor"
		capability "Sensor"
		capability "Health Check"

		command "on"
		command "off"
	}
   
	simulator {
 		// Nothing here, you could put some testing stuff here if you like
	}

    tiles {
    
        // use the state name as the label ("off" and "on")
        standardTile("switch", "device.switch", width: 2, height: 2, decoration: "flat", canChangeBackground: true, canChangeIcon: true) {
            state("off", label: '${currentValue}', action: "switch.on", icon: "st.switches.switch.off", backgroundColor: "#ffffff", nextState: "on", defaultState: true)
            state("on", label: '${currentValue}', action: "switch.off", icon: "st.switches.switch.on", backgroundColor: "#00A0DC", nextState: "off")
        }

        main "switch"
   		details(["switch"])
    }

}

def installed() {
	log.trace "Executing 'installed'"
	initialize()
}

def updated() {
	log.trace "Executing 'updated'"
	initialize()
}

private initialize() {
	log.trace "Executing 'initialize'"
	off()
	sendEvent(name: "healthStatus", value: "online")	
    //sendEvent(name: "DeviceWatch-DeviceStatus", value: "online")
	//sendEvent(name: "DeviceWatch-Enroll", value: [protocol: "cloud", scheme:"untracked"].encodeAsJson(), displayed: false)
}

def on() {
	log.trace "on()"
    sendEvent(name: "switch", value: "on")
    sendEvent(name: "contact", value: "open")
}

def off() {
	log.trace "off()"
  	sendEvent(name: "switch", value: "off")
    sendEvent(name: "contact", value: "closed")
}

private getVersion() {
	"PUBLISHED"
}
