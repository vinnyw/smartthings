/**
 *  ewrew
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
 */

// vid: "generic-switch"
// ocfDeviceType: 'oic.d.switch'
// cstHandler: true, runLocally: true, minHubCoreVersion: '000.021.00001', executeCommandsLocally: true, mnmn: "SmartThings"

metadata {
	definition (name: 'zzz sim button 2', namespace: 'vinnyw', author: 'vinnyw', runLocally: true, minHubCoreVersion: '000.021.00001', executeCommandsLocally: true, ocfDeviceType: 'oic.d.switch' ) {
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
                attributeState "on", label:'${name}', action:"switch.off", icon:"st.Home.home30", backgroundColor:"#00A0DC", nextState:"turningOff"
                attributeState "off", label:'${name}', action:"switch.on", icon:"st.Home.home30", backgroundColor:"#FFFFFF", nextState:"turningOn", defaultState: true
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
	sendEvent(name: "DeviceWatch-DeviceStatus", value: "online")
	sendEvent(name: "healthStatus", value: "online")
	// sendEvent(name: "DeviceWatch-Enroll", value: [protocol: "cloud", scheme:"untracked"].encodeAsJson(), displayed: false)
}

// handle commands
def on() {
	writeLog("Executing 'on()'")
	// TODO: handle 'on' command
}

def off() {
	writeLog("Executing 'off()'")
	// TODO: handle 'off' command
}

private getVersion() {
  return "1.0.0"
}

private writeLog(message) {  
  log.debug ("${device} [v$version]: ${message}")
}

private writeState(message) {
  log.debug ("${device} [v$version]: ${message} settings ${settings}")
  log.debug ("${device} [v$version]: ${message} state ${state}")
}

