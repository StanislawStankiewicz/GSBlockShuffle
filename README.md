# GSBlockShuffle

## About the plugin
Block Shuffle is a fun gamemode in which each player is given a block he has to stand on.

For technical reasons, when the game starts, each player without a team is assigned a new one with their name. Beware of that when trying to change teams after the first game ends.

## Features
- Plenty customizable options
- Teams
  - Compass to easily find your teammates
- Sound effects
  - Sound cues improving gameplay experience
- Gamemode presets
- Easy GUI

## Commands
All commands start with: /blockshuffle or /bs (Commands in *italics* are OP-only)
- */blockshuffle* - Opens GUI
- */blockshuffle start* - Starts the game with current settings
- */blockshuffle end* - Ends the game
- /blockshuffle team - Shows current team
  - /blockshuffle team create \<teamName\> - Creates a team with the specified name
  - /blockshuffle team remove - Removes your current team (You must be the team captain to do this)
  - */blockshuffle team remove /<teamName\>* - Removes specified team
  - /blockshuffle team leave - Leaves the current team
  - /blockshuffle team color \<color\> - Changes the color of the team and team members names
  - */blockshuffle team add \<player\>* - Adds a player to your team without asking.
  - /blockshuffle team invite \<player\> - Invites a player to your team
  - /blockshuffle team join \<team\> - Requests to join the team
  - /blockshuffle team accept - Accepts an invite/join request.
  - /blockshuffle team tp \<player\> - Sends a tp request
  - /blockshuffle team tpaccept - Accepts a tp request
- /blockshuffle tp \<player\> - Same as team tp
- /blockshuffle tpaccept - Same as team tpaccept\
- /blockshuffle settings - Shows the current options
  - */blockshuffle settings preset \<preset\>* - loads a preset
  - */blockshuffle settings load* - Loads settings from settings.yml
  - */blockshuffle settings save* - Saves settings to settings.yml
- */blockshuffle debug \<args\>* - Commands for debugging

## Options
### Block settings
- difficulty: \<integer\> - Current difficulty of the round
- difficultyCap: \<integer\> - Maximum value of difficulty used for incrementation
- increaseDifficulty \<true | false\> - Should difficulty be increased after each round
- increaseEveryNRounds \<integer\> - Will increase difficulty every n-th round. Set to -1 if you want to increase the difficulty at specific rounds
- customIncrease \<list of integers\> - Increment the difficulty at each round in the list
- includeLowerDifficulties \<true | false\> - Should lower difficulties be included
- includeVariants \<true | false\> - Should variants like colors and non-important derivatives (like stairs/slabs etc.) be included
- treatAllAsIndividualBlocks \<true | false\> - Every block has an equal chance of being chosen, regardless of type if true

### Round settings
- roundsPerGame \<integer\> - How many rounds should each game have. Set to -1 for no limit
- roundTime \<integer\> - How long should a round last in seconds
- roundBreakTime \<integer\> - How long should a break between rounds last in seconds
- blockAssignmentMode \<onePerPlayer | onePerTeam | onePerGame\>
  - onePerPlayer - Each player gets a different block
  - onePerTeam - Everyone in the same team gets the same block
  - onePerGame - Everyone gets the same block

### Win settings
- allPlayersRequiredForTeamWin \<true | false\> - Should a team win only if every member found their block
- teamScoreIncrementPerPlayer \<true | false\> - After first player in a team finds their block, should their score still be incremented for other players
- eliminateAfterRound \<true | false\> - Should a player/team be eliminated from the game if they don't find their block
- endGameIfOneTeamRemaining \<true | false\> - Should the game finish if there is only one team remaining
- firstToWin \<true | false\> - Should a round end when first player finds their block

### Teleport settings
- teleportMode \<disabled | amountPerPlayer | amountPerTeam | unlimited\>
  - disabled - Don't allow teleporting between teams
  - amountPerPlayer - Should each player have a certain number of teleports to use
  - amountPerTeam - Should each team have a certain number of teleports to use
  - unlimited - Unlimited teleporting between team members
- amountOfTeleportsPerRound \<integer\> - How many times can a player/team teleport in each round

### Miscelanous
- displaySplashWinnerTitle \<true | false\> - Should the winner(s) get a splash title after the game ends
- showTeamCompass \<true | false\> - Should the team compass be shown for everyone
- muteSounds \<true | false\> - Should all sounds be muted
- disablePvP \<true | false\> - Should PvP be disabled

## Block list
Feel free to rearrange the block_list_categorized.yml file to your liking.
Make sure to follow the files structure so everything works properly.

### block_list_categorized.yml structure:
- category
  - isIncluded - Should this category be included
  - difficulty - Difficulty of the category
  - elements - This will contain all the blocks in the category
  - base
    - isIncluded
    - difficulty
    - elements - If treatAllAsIndividualBlocks = false, all blocks, from this category, will be treated as a single block in the process of randomly getting a block. If this category is chosen a block from it will be randomly selected
  - variant
    - isIncluded
    - difficulty
    - elements - these elements will be joined with the base elements if includeVariants = true

### Getting a random block
If treatAllAsIndividualBlocks = false, all blocks in the "base" and "variant" categories are treated as a single block. This is done to avoid choosing the same type of block too many times (i. e. different kinds of wool).
If includeVariants = true "base" and "variants" are joined together. After this category was randomly chosen, the 

## Installation
1. Download the GSBlockShuffle.jar file.
2. Place the .jar file in your server's plugins folder.
3. Reload the plugins/Restart the server.

## Contribution
If you want to contribute to the development of GSBlockShuffle, please...

Developers: stahu, [MRocin](https://github.com/MRcoin2) 

Sound Deisgn Assistant:

Testing: 
