# Virtual Switch
This is Device Handler for a simple Virtual Switch.  The device will behave and act like a switch, however is not attached to a physical device.  It can be controlled, like any other device, via the SmartThings mobile App or Automations.

The device will raise the following events for each state:


| State        | Switch       |
| ------------ | ------------ |
| Off          | Off          |
| On           | On           |


## Installation 
Install and publish the Device Handler in the usual way via the SmartThings IDE.   
Create a new device in the SmartThings IDE and assign the Device Handler to it. 

> When the device is installed or updated it will set its state to the *Off* position


## Parameters

### Auto Reset?
When this setting is enabled the device will revert back to an *Off* state after 1 second (approximate) . 

In this mode the device can be used as a poor man’s momentary press button by always returning to the default *Off* state.


### Ignore State?
When this setting is enabled the device will always raise an event, even if the device is already in the requested state.

The default behaviour (when disabled) is to only raise an event when the status of the device actually changes value.  

Scenes and Automations to not check the status of a device before sending a status command.  This will allow automations to be triggered multiple times by the same device state.


### Debug Log?
When this setting is enabled the device will write running information, debug messages and errors to the Live Logging console available via the SmartThings Web IDE.

The default behaviour (when disabled) is to only log critical errors to the Live Logging console.  

