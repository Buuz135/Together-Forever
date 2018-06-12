# 1.0.10
+ Update Reskillable Integration (pupnewfster)
+ Fixed GameStages dependency

# 1.0.9
+ Updated Gamestages support to version 2.0

# 1.0.8
+ Added some deep debug for when stuff goes wrong
+ Added a workaround to some mods not having a proper API
+ Added sync actions for Reskillable Level Sync and Unlockable Sync

# 1.0.7
+ Added a safe check for in case the overworld is not loaded

# 1.0.6
+ Forced GameStages sync packet when the stage is not added with "gamestage add" command

# 1.0.5
+ Added even more descriptive commands
+ Made it work in non real servers (LAN)

# 1.0.4
+ Added Reskillable Level team sync (Currently disabled)
+ Added /tf help
+ Fixed a concurrent modification exception that shouldn't happen in the first time

# 1.0.3
+ Team syncing only triggers when joining a server and not when changing dimensions

# 1.0.2
+ Added more safety checks for player info comparison 
+ Added a config option to delay the team syncing action
+ Fixed team owners no being able to kick team members
+ Added more descriptive leave message for the other members of the team
+ Added a forcesync command that allows to ... force sync team data 

# 1.0.1
+ Safe check for missing players when accepting invites
+ Actions now get synced when a player joins a team
+ Added more coloring to some commands msg