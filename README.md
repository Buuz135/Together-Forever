# Together-Forever
Together Forever is a Team API that allows you to sync stuff like advancements or gamestages between the players of your team. By default Advancement Syncing and GameStages Syncing is enabled. If the players of the team were offline when the trigger for a sync happen, that sync will occur when they join to the server.
All the types of sync can be disabled in the config

## Commands
+ `/tofe help` - 
+ `/tofe invite <player_name>` - Invites a player to your team, if you don't have a team it will be created
+ `/tofe accept <player_name>` - Accepts the invite to join that player's team
+ `/tofe decline <player_name>` - Declines the invite to join that player's team
+ `/tofe kick <player_name>` - It kicks a player from your team, only the player that created the team is allowed to run the command
+ `/tofe leave` - Leaves your current team
+ `/tofe info` - Shows information about your team
+ `/tofe forcesync` - Forces the sync of team information

You can also use `/together <sub_command>` or `/togetherforever <sub_command>`