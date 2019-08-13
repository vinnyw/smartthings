
metadata {
        definition (name: "Simulated Momentary Contact Switch", namespace: "vinnyw", author: "Vinny Wadding", ) {
  		capability "Actuator"
		capability "Switch"
		capability "Momentary"
		capability "Sensor"
		capability "Contact Sensor"
	}

	// simulator metadata
	simulator {
		status "open": "contact:open"
		status "closed": "contact:closed"

	}

	// UI tile definitions
	tiles {
		standardTile("switch", "device.switch", width: 2, height: 2, canChangeIcon: true) {
			state "off", label: 'Push', action: "momentary.push", backgroundColor: "#ffffff", nextState: "on"
			state "on", label: 'Push', action: "momentary.push", backgroundColor: "#53a7c0"
		}
		main "switch"
		details "switch"
	}
}

def parse(String description) {
}

def push() {
	sendEvent(name: "switch", value: "on", isStateChange: true, display: false)
	sendEvent(name: "switch", value: "off", isStateChange: true, display: false)	
	sendEvent(name: "contact", value: "open", isStateChange: true)	
	sendEvent(name: "momentary", value: "pushed", isStateChange: true)
}

def on() {
	push()
}

def off() {
	push()
}

