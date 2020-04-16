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

}


// parse events into attributes
def parse(String description) {
	writeLog("Parsing '${description}'")
	// TODO: handle 'contact' attribute
	// TODO: handle 'switch' attribute
	//sendEvent(name: "DeviceWatch-DeviceStatus", value: "online")

}

def installed() {
	writeLog("Executing 'installed()'")
	writeState("installed()")
	initialize()
    off()
}

def updated() {
	writeLog("Executing 'updated()'")
	writeState("updated()")
	initialize()
}

private initialize() {
	writeLog("Executing 'initialize()'")
	//sendEvent(name: "DeviceWatch-DeviceStatus", value: "online")
	//sendEvent(name: "healthStatus", value: "online")
	//sendEvent(name: "DeviceWatch-Enroll", value: [protocol: "cloud", scheme:"untracked"].encodeAsJson(), displayed: false)
}

// handle commands
def on() {
	writeLog("Executing 'on()'")
	// TODO: handle 'on'
  	def cmds = [] 
    cmds << createEvent(name: "switch", value: "on", isStateChange: true)
    cmds << createEvent(name: "contact", value: "close", isStateChange: true)
   	sendEvents(cmds)
}

def off() {
	writeLog("Executing 'off()'")
	// TODO: handle 'off' command
 	def cmds = []
    cmds << createEvent(name: "switch", value: "off", isStateChange: true)
    cmds << createEvent(name: "contact", value: "open", isStateChange: true)
   	sendEvents(cmds)
}   

private sendEvents(cmds) {  
  log.debug ("${device} [v$version]: ${cmds}")
  
  	cmds.each { 
    	cmd -> sendEvent(cmd)
                writeLog(cmd)

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
  return "1.0.7"
}

