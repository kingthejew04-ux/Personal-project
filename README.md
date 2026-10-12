# AutoRTP (Fabric, Minecraft 26.2, client-side)

Repeats `/rtp` until you land in the biome you pick, then stops.

## Commands
- `/autortp start <biome>`  e.g. `/autortp start desert`, `/autortp start cherry_grove`
- `/autortp stop`
- `/autortp delay <seconds>`  time between /rtp attempts (2-60, default 5)
- `/autortp status`

## Build
1. Install JDK 25 and Gradle 9.x
2. Run `gradle build` in this folder
3. Put `build/libs/autortp-1.0.0.jar` in your `mods` folder with Fabric Loader and Fabric API.

Client-side only: works on any server that has an /rtp command. Raise the delay if the server has an /rtp cooldown.
