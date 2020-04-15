/**
 *  Alexa Simulated Switch
 *
 *  Copyright 2020 Vinny Wadding
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 **/

metadata {
	definition (name: 'zzz sim button 2', namespace: 'vinnyw', author: 'vinnyw', runLocally: true, executeCommandsLocally: false, minHubCoreVersion: '000.021.00001', ocfDeviceType: 'oic.d.switch' ) {
        capability "Actuator"
        capability "Sensor"
		capability "Contact Sensor"
		capability "Switch"
	}

	simulator {
		// TODO: define status and reply messages here
	}

    tiles(scale: 2) {
        multiAttributeTile(name:"switch", type: "lighting", width: 6, height: 4, canChangeIcon: true){
            tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
           		attributeState "off", label:'${name}', action:"switch.on", icon: "st.switches.switch.off", backgroundColor:"#FFFFFF", defaultState: true
                attributeState "on", label:'${name}', action:"switch.off", icon: "st.switches.switch.on", backgroundColor:"#00A0DC"
            }
        }

        main(["switch"])
        details(["switch"])

    }

	preferences {
    	section {
         	input type: "paragraph", element: "paragraph", title: "Logging:", description: "General settings."
			input name: "displayDebug", type: "boolean", title: "Debug", defaultValue: false, displayDuringSetup: false
     	}
	}

}
   
// parse events into attributes
def parse(String description) {
	if ("true" == displayDebug) {
		writeLog("Parsing '${description}'")
 	}
}

def installed() {
	if ("true" == displayDebug) {
		writeLog("Executing 'installed()'")
		writeState("installed()")
  	}
	initialize()
    off()
}

def updated() {
	if ("true" == displayDebug) {
    	writeLog("Executing 'updated()'")
  		writeState("updated()")
	}
	initialize()
}

private initialize() {
	if ("true" == displayDebug) {
		writeLog("Executing 'initialize()'")
	}
 	def cmds = []
    cmds << createEvent(name: "DeviceWatch-DeviceStatus", value: "online")
    cmds << createEvent(name: "healthStatus", value: "online")
	//cmds << createEvent(name: "DeviceWatch-Enroll", value: [protocol: "cloud", scheme:"untracked"].encodeAsJson(), displayed: false)
   	sendEvents(cmds)
}

// handle commands
def on() {
	if ("true" == displayDebug) {
		writeLog("Executing 'on()'")
    }
  	def cmds = [] 
    cmds << createEvent(name: "switch", value: "on", isStateChange: true)
    cmds << createEvent(name: "contact", value: "close", isStateChange: true)
   	sendEvents(cmds)
}

def off() {
	if ("true" == displayDebug) {
   		writeLog("Executing 'off()'")
    }
 	def cmds = []
    cmds << createEvent(name: "switch", value: "off", isStateChange: true)
    cmds << createEvent(name: "contact", value: "open", isStateChange: true)
   	sendEvents(cmds)
}   

private sendEvents(cmds) {  
	cmds.each { 
  		cmd -> sendEvent(cmd)
		if ("true" == displayDebug) {
   			writeLog(cmd)
        }
  	}
}

private writeLog(message) {  
	log.debug ("${device} [v$version]: ${message}")
}

private writeState(message) {
	log.debug ("${device} [v$version]: ${message} settings ${settings}")
	log.debug ("${device} [v$version]: ${message} state ${state}")
}

private getVersion() {
  return "1.0.11f"
}

