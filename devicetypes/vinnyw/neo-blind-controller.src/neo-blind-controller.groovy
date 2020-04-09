/**
 *  FIBARO Roller Shutter 2
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *	  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */

def dhVersion() { return "1.0.2" } 

metadata {
    definition (name: "Neo Blind Controller", namespace: "vinnyw", author: "vinnyw", ocfDeviceType: "oic.d.blind", mnmn: "SmartThings", vid: "generic-shade-2") {
        capability "Actuator"
        capability "Battery"
        capability "Configuration"
        capability "Refresh"
        capability "Health Check"
        capability "Window Shade"
        capability "Switch"
        capability "Switch Level"

        attribute("replay", "enum")
        attribute("battLife", "enum")

        command "cont"

        fingerprint profileId: "0104", inClusters: "0000, 0001, 0003, 0006, FC00, DC00, 0102", deviceJoinName: "Curtain", manufacturer: "Rooms Beautiful", model: "C001"
    }

    preferences {
        input name: "invert", type: "bool", title: "Invert Direction", description: "Invert Curtain Direction", defaultValue: false, displayDuringSetup: false, required: true
    }

    tiles(scale: 2) {
        multiAttributeTile(name: "windowShade", type: "generic", width: 6, height: 4) {
            tileAttribute("device.windowShade", key: "PRIMARY_CONTROL") {
                attributeState "open", label: 'Open', action: "close", icon: "http://www.ezex.co.kr/img/st/window_open.png", backgroundColor: "#00A0DC", nextState: "closing"
                attributeState "closed", label: 'Closed', action: "open", icon: "http://www.ezex.co.kr/img/st/window_close.png", backgroundColor: "#ffffff", nextState: "opening"
                attributeState "partially open", label: 'Partially open', action: "close", icon: "http://www.ezex.co.kr/img/st/window_open.png", backgroundColor: "#d45614", nextState: "closing"
                attributeState "opening", label: 'Opening', action: "close", icon: "http://www.ezex.co.kr/img/st/window_open.png", backgroundColor: "#00A0DC", nextState: "closing"
                attributeState "closing", label: 'Closing', action: "open", icon: "http://www.ezex.co.kr/img/st/window_close.png", backgroundColor: "#ffffff", nextState: "opening"
            }
            tileAttribute("device.battLife", key: "SECONDARY_CONTROL") {
                attributeState "full", icon: "https://raw.githubusercontent.com/gearsmotion789/ST-Images/master/full.png", label: ""
                attributeState "medium", icon: "https://raw.githubusercontent.com/gearsmotion789/ST-Images/master/medium.png", label: ""
                attributeState "low", icon: "https://raw.githubusercontent.com/gearsmotion789/ST-Images/master/low.png", label: ""
                attributeState "dead", icon: "https://raw.githubusercontent.com/gearsmotion789/ST-Images/master/dead.png", label: ""
            }
            tileAttribute("device.level", key: "SLIDER_CONTROL") {
                attributeState "level", action: "switch level.setLevel"
            }
        }
        standardTile("contPause", "device.replay", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
            state "pause", label: "Pause", icon: 'https://raw.githubusercontent.com/gearsmotion789/ST-Images/master/pause.png', action: 'pause', backgroundColor: "#e86d13", nextState: "cont"
            state "cont", label: "Cont.", icon: 'https://raw.githubusercontent.com/gearsmotion789/ST-Images/master/play.png', action: 'cont', backgroundColor: "#90d2a7", nextState: "pause"
        }
        standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat", width: 2, height: 1) {
            state "default", label: "", action: "refresh.refresh", icon: "st.secondary.refresh"
        }

        main "windowShade"
        details(["windowShade", "contPause", "refresh"])
    }
}


