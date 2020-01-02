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
 */
metadata {

    definition (name: "Simulated Switch (v2)", namespace: "vinnyw", author: "Vinny Wadding", mnmn: "SmartThings") {
        capability "Switch"
        //capability "Relay Switch"
        //capability "Sensor"
        //capability "Actuator"
        //capability "Health Check"        
        capability "Contact Sensor"

        command "on"
        command "off"
        command "markDeviceOnline"
        command "markDeviceOffline"
    }

    tiles {
        standardTile("switch", "device.switch", width: 2, height: 2, canChangeIcon: false) {
            state "off", label: '${currentValue}', action: "switch.on", icon: "st.switches.switch.off", backgroundColor: "#ffffff"
            state "on", label: '${currentValue}', action: "switch.off", icon: "st.switches.switch.on", backgroundColor: "#00A0DC"
        }
        main "switch"
        details(["switch"])
    }
}

def installed() {
    log.trace "Executing 'installed'"
    markDeviceOnline()
    off()
    initialize()
}

def updated() {
    log.trace "Executing 'updated'"
    initialize()
}

def markDeviceOnline() {
    setDeviceHealth("online")
}

def markDeviceOffline() {
    setDeviceHealth("offline")
}

private setDeviceHealth(String healthState) {
    log.debug("healthStatus: ${device.currentValue('healthStatus')}; DeviceWatch-DeviceStatus: ${device.currentValue('DeviceWatch-DeviceStatus')}")
    // ensure healthState is valid
    List validHealthStates = ["online", "offline"]
    healthState = validHealthStates.contains(healthState) ? healthState : device.currentValue("healthStatus")
    // set the healthState
    sendEvent(name: "DeviceWatch-DeviceStatus", value: healthState)
    sendEvent(name: "healthStatus", value: healthState)
}

private initialize() {
    log.trace "Executing 'initialize'"
    sendEvent(name: "DeviceWatch-Enroll", value: [protocol: "cloud", scheme:"untracked"].encodeAsJson(), displayed: false)
}

def parse(description) {
}

def on() {
/* isStateChange: true - send event even if device status has not changed
 isStateChange: false  - only when device has changed state
*/
	log.debug "$version on()"
    sendEvent(name: "switch", value: "on", isStateChange: false, Displayed: true)
	sendEvent(name: "contact", value: "open", isStateChange: false, Displayed: false)
 
}

def off() {
    log.debug "$version off()"
    sendEvent(name: "switch", value: "off", isStateChange: false, Displayed: true)
 	sendEvent(name: "contact", value: "close", isStateChange: false, Displayed: false)
}


private getVersion() {
    "PUBLISHED"
}
