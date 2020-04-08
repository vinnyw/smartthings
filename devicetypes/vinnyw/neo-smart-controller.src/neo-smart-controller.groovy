/*
 * Neo Smart Controller
 *
 * Calls URIs with HTTP GET for shade open/close/stop/favourite using the Neo Smart Controller
 * 
 * Assumption: The blinds (or room of blinds) you're controlling all take the 
 * same amount of time to open/close (some may be a little faster/slower than
 * the others, but they should all generally take the same amount of time to
 * otherwise, create another virtual driver for ones that are slower/faster). 
 * 
 * To use: 
 * 1) Run a stopwatch to time how long it takes for the blinds to close (in
 * seconds)
 * 2) Open the blinds completely then time how long it takes to get to your
 * favorite setting) 
 * 
 * Input these values in the configuration, rounding down.  Your device will
 * like a dimmable bulb to Alexa, thus you should be able to say "Alexa, turn
 * the living room blinds to 50%" and it will go in the middle (or close). 
 *
 * Keep in mind, in order for this to work you have to always control the
 * blinds through Hubitat and not the Neo App or RF remote. 
 *  
 * Based on the Hubitat community driver httpGetSwitch
 */







metadata {
    definition(name: "Neo Smart Controller", namespace: "vinnyw", author: "vinnyw") {
        capability "WindowShade"
		capability "Switch"
		capability "Actuator"
	 	//capability "Change Level"   
		capability "Switch Level"
		
		command "stop"
		command "favorite"
		command "up"
		command "down"
    }

    preferences {
        section("URIs") {
            input "blindCode", "text", title: "Blind or Room Code (from Neo App\n)", required: true
            input "controllerID", "text", title: "Controller ID (from Neo App)\n", required: true
            input "controllerIP", "text", title: "Controller IP Address (from Neo App)\n", required: true
            input "timeToClose", "number", title: "Time in seconds it takes to close the blinds completely\n", required: true
            input "timeToFav", "number", title: "Time in seconds it takes to reach your favorite setting when closing the blinds\n", required: true
            input name: "logEnable", type: "bool", title: "Enable debug logging\n", defaultValue: true
        }
    }

    tiles(scale: 2) {
        multiAttributeTile(name: "windowShade", type: "generic", width: 6, height: 4) {
            tileAttribute("device.windowShade", key: "PRIMARY_CONTROL") {
                attributeState("closed", label: 'closed', action: "windowShade.open", icon: "st.doors.garage.garage-closed", backgroundColor: "#A8A8C6", nextState: "opening")
                attributeState("open", label: 'open', action: "windowShade.close", icon: "st.doors.garage.garage-open", backgroundColor: "#F7D73E", nextState: "closing")
                attributeState("closing", label: '${name}', action: "windowShade.open", icon: "st.contact.contact.closed", backgroundColor: "#B9C6A8")
                attributeState("opening", label: '${name}', action: "windowShade.close", icon: "st.contact.contact.open", backgroundColor: "#D4CF14")
                attributeState("partially open", label: 'partially\nopen', action: "windowShade.close", icon: "st.doors.garage.garage-closing", backgroundColor: "#D4ACEE", nextState: "closing")
            }
            tileAttribute("device.level", key: "SLIDER_CONTROL") {
                attributeState("level", action: "setLevel")
            }
        }
        standardTile("open", "open", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
            state("open", label: 'open', action: "windowShade.open", icon: "st.contact.contact.open")
        }
        standardTile("close", "close", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
            state("close", label: 'close', action: "windowShade.close", icon: "st.contact.contact.closed")
        }
        standardTile("stop", "stop", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
            state("stop", label: 'stop', action: "windowShade.stop", icon: "st.illuminance.illuminance.dark")
        }
        standardTile("refresh", "command.refresh", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
            state "default", label: " ", action: "refresh.refresh", icon: "https://www.shareicon.net/data/128x128/2016/06/27/623885_home_256x256.png"
        }
        standardTile("home", "device.level", width: 2, height: 2, decoration: "flat") {
            state "default", label: "home", action:"presetPosition", icon:"st.Home.home2"
        }
        main(["windowShade"])
        details(["windowShade", "open", "stop", "close", "refresh"])
    }
}

def date() {
	//def date = new Date().getTime().toString().drop(6)
    def origdate = new Date().getTime().toString().drop(6)  // API docs say only 7 chars 
    def random = Math.random().toString().reverse().take(4) // get four random #'s
    def date = origdate.toInteger() + random.toInteger()    // add 4 random #'s to millisecs
    if (logEnable) log.debug "Using ${date}"
	return date
}

def get(url,state) {
   try {
        httpGet(url) { resp ->
            if (resp.success) {
                sendEvent(name: "windowShade", value: "${state}", isStateChange: true)
           }
           if (logEnable)
                if (resp.data) log.debug "${resp.data}"
        }
    } catch (Exception e) {
        log.warn "Call to ${url} failed: ${e.message}"
    }
}

def logsOff() {
    log.warn "debug logging disabled..."
    device.updateSetting("logEnable", [value: "false", type: "bool"])
}

def installed() {
    log.info "installed..."
	if (!controllerID || !controllerIP || !blindCode || !timeToClose || !timeToFav) {
		log.error "Please make sure controller ID, IP, blind/room code, time to close and time to favorite are configured." 
	}
    log.warn "debug logging is: ${logEnable == true}"
    if (logEnable) runIn(1800, logsOff)
}

def updated() {
    log.info "updated..."
	if (!controllerID || !controllerIP || !blindCode || !timeToClose || !timeToFav) {
		log.error "Please make sure controller ID, IP, blind/room code, time to close and time to favorite are configured." 
	}
    log.warn "debug logging is: ${logEnable == true}"
    if (logEnable) runIn(1800, logsOff)
}

def parse(String description) {
    if (logEnable) log.debug(description)
}

def up() {
	startLevelChange("up")
}

def down() {
	startLevelChange("down")
}
	
def on() {
	open() 
}

def off() {
	close()
}

def close() {
    url = "http://" + controllerIP + ":8838/neo/v1/transmit?command=" + blindCode + "-dn&id=" + controllerID + "&hash=" + date()
    if (logEnable) log.debug "Sending close GET request to ${url}"
    sendEvent(name: "windowShade", value: "closing", isStateChange: true)
	get(url,"closed")
	state.level=0
    state.secs=timeToClose
    sendEvent(name: "level", value: "${state.level}", isStateChange: true)
}

def open() {
    url = "http://" + controllerIP + ":8838/neo/v1/transmit?command=" + blindCode + "-up&id=" + controllerID + "&hash=" + date()
    if (logEnable) log.debug "Sending open GET request to ${url}"
    sendEvent(name: "windowShade", value: "opening", isStateChange: true)
	get(url,"open")
	state.level=100
    state.secs=0
    sendEvent(name: "level", value: "${state.level}", isStateChange: true)
}

def stop() {
    // todo, it would be nice if we could start a timer when someone opens/closes so if they stop we have an idea of where the shade is
    // then we could reflect it by setting the level here... 
    url = "http://" + controllerIP + ":8838/neo/v1/transmit?command=" + blindCode + "-sp&id=" + controllerID + "&hash=" + date()
    if (logEnable) log.debug "Sending stop GET request to ${url}"
	get(url,"partially open")
}

def stopPosition() {
    url = "http://" + controllerIP + ":8838/neo/v1/transmit?command=" + blindCode + "-sp&id=" + controllerID + "&hash=" + date()
    if (logEnable) log.debug "Sending stop GET request to ${url}"
	get(url,"partially open")
    sendEvent(name: "level", value: "${state.newposition}", isStateChange: true)
    if (logEnable) log.debug "Stopped ${state.newposition} ${state.difference}"
    state.level=state.newposition
    state.difference=0
}

def runAndStop() {
    if (state.direction == "up") {
        url = "http://" + controllerIP + ":8838/neo/v1/transmit?command=" + blindCode + "-up&id=" + controllerID + "&hash=" + date()
    } else {
        url = "http://" + controllerIP + ":8838/neo/v1/transmit?command=" + blindCode + "-dn&id=" + controllerID + "&hash=" + date()
    }
    if (logEnable) log.debug "Adjusting ${state.direction} to ${state.newposition} for ${state.difference} request to ${url}"
    get(url,"partially open")   
    runInMillis(state.difference.toInteger(),stopPosition)
}

def favorite() {
    url = "http://" + controllerIP + ":8838/neo/v1/transmit?command=" + blindCode + "-gp&id=" + controllerID + "&hash=" + date()
    if (logEnable) log.debug "Sending favorite GET request to ${url}"
    state.secs=timeToFav
    state.level=100-((timeToFav/timeToClose)*100)
	get(url,"partially open")
    sendEvent(name: "level", value: "${state.level}", isStateChange: true)
}

def setPosition(position) { // timeToClose= closed/down, 0=open/up
    secs = timeToClose-((position/100)*timeToClose)  // get percentage based on how long it takes to open.     
    if (logDebug) log.debug "Setting Position for ${blindCode} to ${position} (maps to travel time from open of ${secs} secs)"
    if (secs >= timeToClose) {
		secs = timeToClose
	}
    if (position != state.level)  {
        if (logDebug) log.debug "Position: ${position}  StateLevel: ${state.level}  StateSecs: ${state.secs} Secs: ${secs}"
            if ((position > state.level) || (state.secs > secs)) { // requested location is more closed than current. 
                if (position == 100) {
                    open()
                    state.level=100
                    state.secs=0
                } else {
                    pos = ((state.secs - secs) * 1000) //-2000
                    if (pos < 1000) {
                         pos = 1000
                    }
                    state.direction = "up"
                    state.difference = pos
                    state.newposition = position
                    state.secs = secs
                    if (logDebug) log.debug "Opening... Stopping at ${pos} secs"
                    runInMillis(10,runAndStop)
                }
            } else {  // location is less closed than current
                if (position == 0) {
                    close()
                    state.level=0
                    state.secs=timeToClose
                } else {
                    def pos = ((secs - state.secs)*1000) //-2000
                    if (pos < 1000) {
                         pos = 1000
                    }
                    state.direction = "down"
                    state.difference = pos
                    state.newposition = position
                    state.secs = secs
                    if (logDebug) log.debug "Closing... Stopping at ${pos} secs"
                    runInMillis(10,runAndStop)
      
                }
            }
      }
}

def startLevelChange(direction) {
	// https://github.com/hubitat/HubitatPublic/blob/master/examples/drivers/genericComponentDimmer.groovy 
    if (direction == "up") {
        url = "http://" + controllerIP + ":8838/neo/v1/transmit?command=" + blindCode + "-mu&id=" + controllerID + "&hash=" + date()
    } else {
        url = "http://" + controllerIP + ":8838/neo/v1/transmit?command=" + blindCode + "-md&id=" + controllerID + "&hash=" + date()
    }
    if (logEnable) log.debug "Sending startLevel Change ${direction} GET request to ${url}"
    get(url,"partially open")
}

def setLevel(level) {
    setPosition(level)
}