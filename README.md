# AsgardAscension (DISCONTINUED)
Custom Minecraft plugin made for Asgard Ascenscion server. It was core plugin of the prison type server. First version was released in June, 2015 and project was discontinued in late 2016. The main objective was making the plugin easy to use for both players and admins.  Most of the features has their own GUI. By the request, flat-files were used for data saving.

## Table of Contents
* [Features](#features)
* [Dependencies](#dependencies)
* [Commands](#dependencies)
* [Permissions](#permissions)

## Features
### Challenges
Instead of regular way of rankups, where player needs only to have some cash to obtain a new rank, this system aims to make the process more interesting. For rich and lazy players, they can skip the challenge by paying 2x more money. However, the brave ones can accept the challenge. The challenges are hand-made by the server administrators with built-in GUI setup, which allows to specify type, price, rewards and much more. Currently, there are 3 types of challenges:
* Fight (defeating specified amount of mobs in arena with your own items. Upon death, items are not lost and money is returned back to player)
* Parkour (Player must get through various puzzles and reach the noteblock in order to complete it)
* Maze (Works the same way as parkour)
### Prestiging
When player completes all challenges, he may prestige and get back to rank 0 to complete all the challenges again. Max prestige level is configurable and so are the perks.
### Ragnorak
Once in a while, mysterious event starts. Loot spawns randomly in predefined chest locations for players to collect. Great event for massive PVP.
### Food of the Gods
Players can craft or buy items and get their perks for some amount of time.
### God Tokens
God Tokens are premium currency. It serves many purposes but the most popular one is buying runes, abilities, and food of the gods.
### Tribes
Playes can create their own tribes (clans). Tribe has its communal chest, chat, diplomacy, ranks.
### Custom Enchants
Enchants made for donors to make them more OP. As of now, there are 4 custom enchants:
* Wither Damage (Upon hitting enemy, he gets wither effect for 10 seconds)
* Fire Damage (Enemy is set on fire for 5 seconds)
* Poison Damage (Enemy is poisoned for 10 seconds)
* 2x2 Explosion (By destroying block, 3 more blocks around are destroyed)

## Dependencies
**NOTE:** Different versions may or may not break the plugin

* [DeluxeChat **1.10.0**](https://www.spigotmc.org/resources/deluxechat.1277/)
* [EffectLib **5.0**](https://dev.bukkit.org/projects/effectlib)
* [MineResetLite **0.4.6**](https://www.spigotmc.org/resources/mineresetlite.5773/)
* [MVdWPlaceholderAPI **1.0.0**](https://www.spigotmc.org/resources/mvdwplaceholderapi.11182/)
* [PlotSquared **3.5.0-SNAPSHOT-95f8aaa**](https://www.spigotmc.org/resources/plotsquared.1177/)
* [ProtocolLib **3.6.4**](https://www.spigotmc.org/resources/protocollib.1997/)
* [Vault **1.5.6**](https://dev.bukkit.org/projects/vault)
* [WorldEdit **6.1.3**](https://dev.bukkit.org/projects/worldedit)
* [WorldGuard **6.1**](https://dev.bukkit.org/projects/worldguard)

## Commands
* /asgardascension (/aa, /asg)
* /rankup
* /tribe
* /token
* /prestige
* /ragnorak
* /rank
* /fog
* /challenge
* /tokenwarp

## Permissions
* **asgardascension.admin**
  * Default: **OP**
  * Grants access to:
    * /asgardascension (/aa, /asg)
    * /prestige location
    * /rank
    * /token give
    * /token remove
    * /token set
    * /tribe socialspy (/tribe ss)
    * /tokenwarp
* **asgardascension.staff**
  * Default: **OP**
  * Grants access to:
    * /tribe socialspy (/tribe ss)
