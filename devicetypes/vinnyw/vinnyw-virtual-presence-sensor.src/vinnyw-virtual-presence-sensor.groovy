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
    definition (name: "Vinnyw - Virtual Presence Sensor", namespace: "smartthings", author: "vinnyw", runLocally: true, minHubCoreVersion: '000.021.00001', executeCommandsLocally: true, mnmn: "SmartThings", vid: "generic-switch") {
        capability "Actuator"
        capability "Sensor"
        capability "Switch"
        capability "Presence Sensor"
    }

    preferences {}

    tiles(scale: 2) {
        multiAttributeTile(name:"switch", type: "lighting", width: 6, height: 4, canChangeIcon: true){
            tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
                attributeState "on", label:'${name}', action:"switch.off", icon:"st.Home.home30", backgroundColor:"#00A0DC", nextState:"turningOff"
                attributeState "off", label:'${name}', action:"switch.on", icon:"st.Home.home30", backgroundColor:"#FFFFFF", nextState:"turningOn", defaultState: true
                attributeState "turningOn", label:'Turning On', action:"switch.off", icon:"st.Home.home30", backgroundColor:"#00A0DC", nextState:"turningOn"
                attributeState "turningOff", label:'Turning Off', action:"switch.on", icon:"st.Home.home30", backgroundColor:"#FFFFFF", nextState:"turningOff"
            }
        }

        standardTile("explicitOn", "device.switch", width: 2, height: 2, decoration: "flat") {
            state "default", label: "On", action: "switch.on", icon: "st.Home.home30", backgroundColor: "#ffffff"
        }
        standardTile("explicitOff", "device.switch", width: 2, height: 2, decoration: "flat") {
            state "default", label: "Off", action: "switch.off", icon: "st.Home.home30", backgroundColor: "#ffffff"
        }

        main(["switch"])
        details(["presence", "switch", "explicitOn", "explicitOff"])

    }
}

def parse(String description) {
    def pair = description.split(":")
    createEvent(name: pair[0].trim(), value: pair[1].trim())
}

def installed() {
    initialize()
}

def updated() {
    initialize()
}

def initialize() {
    sendEvent(name: "DeviceWatch-DeviceStatus", value: "online")
    sendEvent(name: "healthStatus", value: "online")
    sendEvent(name: "DeviceWatch-Enroll", value: [protocol: "cloud", scheme:"untracked"].encodeAsJson(), displayed: false)
}

def on() {
    log.trace "Executing 'arrived'"
    sendEvent(name: "switch", value: "on", isStateChange: true)
    sendEvent(name: "presence", value: "present")
}

def off() {
    log.trace "Executing 'departed'"    
    sendEvent(name: "switch", value: "off", isStateChange: true)
    sendEvent(name: "presence", value: "not present")
}

