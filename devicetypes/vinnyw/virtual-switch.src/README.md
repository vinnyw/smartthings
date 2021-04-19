# Virtual Switch


## Parameters

### Auto Reset?
When this setting is enabled the device will revert back to an off state after 1 second. 

In this mode the device can be used as a poor man’s momentary press button by always returning to the default Off state.


### Ignore State?
When this setting is enabled the device will always raise an event, even if the device is already at that state.

The default behaviour (when off) is to only raise an event when the status of the device actually changes value.  

Scenes and Automations to not check the status of a device before sending a status command.  This can lead to duplicate events or automations being triggered.


### Debug Log?
When this setting is enabled the device will write running information, debug messages and errors to the Live Logging console available via the SmartThings Web IDE.

The default behaviour (when off) is to only log critical errors to the Live Logging console.  


