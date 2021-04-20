# Virtual Switch


## Parameters

### Auto Reset?
When this setting is enabled the device will revert back to an *Off* state after 1 second (approximate) . 

In this mode the device can be used as a poor man’s momentary press button by always returning to the default *Off* state.


### Ignore State?
When this setting is enabled the device will always raise an event, even if the device is already in the requested state.

The default behaviour (when Off) is to only raise an event when the status of the device actually changes value.  

Scenes and Automations to not check the status of a device before sending a status command.  This will allow automations to be triggered multiple times by the same device state.


### Debug Log?
When this setting is enabled the device will write running information, debug messages and errors to the Live Logging console available via the SmartThings Web IDE.

The default behaviour (when Off) is to only log critical errors to the Live Logging console.  

