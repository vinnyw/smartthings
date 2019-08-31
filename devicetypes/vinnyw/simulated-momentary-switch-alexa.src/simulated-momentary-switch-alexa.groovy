/*
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

 	definition (name: "Simulated Momentary Switch (Alexa)", namespace: "vinnyw", author: "Vinny Wadding", runLocally: true, mnmn: "SmartThings", vid: "generic-switch") {
        capability "Switch"
        capability "Relay Switch"
 		capability "Contact Sensor"
        capability "Sensor"
        capability "Actuator"
    }

    tiles {
        standardTile("switch", "device.switch", width: 2, height: 2, canChangeIcon: true) {
            state "off", label: '${currentValue}', action: "switch.on", icon: "st.switches.switch.off", backgroundColor: "#ffffff"
            state "on", label: '${currentValue}', action: "switch.off", icon: "st.switches.switch.on", backgroundColor: "#00A0DC"
        }
        main "switch"
        details(["switch"])
    }

}

def installed() {
    log.trace "Executing 'installed'"
    initialize()
    off()
}

def updated() {
    log.trace "Executing 'updated'"
    initialize()
}

private initialize() {
    log.trace "Executing 'initialize'"
    sendEvent(name: "DeviceWatch-Enroll", value: [protocol: "cloud", scheme:"untracked"].encodeAsJson(), displayed: false)
}

def parse(description) {
}

def on() {
    log.debug "$version on()"
    sendEvent(name: "switch", value: "on", isStateChange: true, display: false)
	sendEvent(name: "contact", value: "open", isStateChange: true, display: false)
    off()
}

def off() {
    log.debug "$version off()"
    sendEvent(name: "switch", value: "off", isStateChange: true, display: false)
}

