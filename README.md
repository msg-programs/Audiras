# === Readme - Audiras v1.2 ===

## === THIS IS STILL WIP UNTIL A VERSION IS RELEASED

## == By:  
* msg (msg-programs on github)

## == What:  
* A minimalistic recorder for internet radio streams.

## == Needed:  
* Nothing, Java 13 runtime is bundled with the release.

## == Using:
* Mp3agic by mpatric, see https://github.com/mpatric/mp3agic and "mp3agic license.txt"

## == Usage:  
### Recorder tab
* Left side: List of all streams ready to record, master buttons to start/stop all recorders.
* Right side: Stream info, individual start/stop button, button to remove stream from recorder list.

### List tab:
* Left side: List of all streams available.
* Right side Stream info, button to add stream to recording list, button to add stream currently not in the list.

### Settings tab:
* Checkboxes: Should the program start on Windows startup? When the program is starting, start all recordings? When the program is run, show the window? 
* Dropdown and text field: Set recording limit
* Save button: save settings

## == Generated files:  
* in directory /data: settings.ini (settings file)
* all recorded songs go to folders named like the station inside the given directory (default: same as .jar)

## == Final notes:
* Currently only supports audio/mpeg streams