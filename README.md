# AutoRTP (Fabric, Minecraft 26.2, client-side)

Repeats `/rtp` until you land in the biome you pick, then stops.

## Commands
- `/autortp start <biome>`  e.g. `/autortp start desert`, `/autortp start cherry_grove`
- `/autortp stop`
- `/autortp delay <seconds>`  time between /rtp attempts (2-60, default 5)
- `/autortp status`



Client-side only: works on any server that has an /rtp command. Raise the delay if the server has an /rtp cooldown.
