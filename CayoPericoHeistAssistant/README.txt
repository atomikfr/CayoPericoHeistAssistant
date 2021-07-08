
__== Cayo Perico Heist Assistant ==__
                           v 0.10


This is an external tool showing you the current state of "Cayo Perico" Heist
for any session connected player.
You can also change some of YOUR heist parameters.

WARNING __________________________________________________
|                                                         |
|  Changing any parameter of the game can leads to a ban. |
|  Use at your own risk.                                  |
|_________________________________________________________|


## Requirements #####################

 - Java Runtime Environment >= 11
   suggested JRE: https://cdn.azul.com/zulu/bin/zulu11.43.55-ca-jdk11.0.9.1-win_x64.msi
   if you are running windows x64
   or get a JDK from https://adoptopenjdk.net/releases.html

 - GTA5 Online v 1.52


## How to use #######################

 - Run GTA5, go Online

 - Run CayoPericoHeistAssistant.bat

 - Select a player of your session in the list to see his loot / map of equipement.

   The "reload" button allows you to refresh the player list.
   The "reset" button is used to restart the submarine computer after modifying your heist configuration. 

   Each point represent a current location (spotted or not).

   - Equipment
   Guard Uniform        : Yellow
   Grappling Equipement : Magenta
   Bolt Cutters         : Gray
   Guard Truck          : Pink
 
   - Additional Loot
   Cash      : Blue
   Cocaine   : White
   Weed      : Green
   Gold      : Orange
   Paintings : Light blue

 - The left menu allows you to get information about what is made during preparation.
   "Cuts" only works on YOUR heist before launching the mission.
   Don't change cuts in game after setting in tool if total > 100%.

 - If you select YOUR player in list, you can change and save equipment, approach, scopped loot ...
   Beware when selecting a weapon, you MUST select "suppressors" also.

 - Heist Menu allows you to change the main loot value and your bagsize DURING the heist.
   bagsize can be changed on any heist (yours on others).
   Loot value only works for yours.

## Known Issues #####################

 - Select a weapon without suppressors don't trigger the mission ready.

 - Compound loot placement are erroneous for now. 
   Still have to work on this.

- A player's final take over 5M won't be payed out. This is a limit of the heist.

## Thanks ###########################

 - polivias
 - josephsmendoza
 - unknowncheats Community

