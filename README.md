# GSBlockShuffle

## About the plugin
Block Shuffle is a fun gamemode in which each player is given a block he has to stand on.

For technical reasons, when the game starts, each player without a team is assigned a new one with their name. Beware of that when trying to change teams after the first game ends.

## Features
- Gamemode presets
- Teams
  - Compass to easily find your teammates
- Plenty of customizable options
  - Easy GUI
- Sound effects
  - Sound cues improving gameplay experience

## Installation
1. Download the [GSBlockShuffle.jar](https://github.com/stahuOfficial/GSBlockShuffle/releases/latest) file.
2. Place the .jar file in your server's plugins folder.
3. Reload the plugins/Restart the server.

## Commands
All commands start with: `/blockshuffle` or `/bs` (commands marked with <span style="color:red">*</span> are OP-only by default)
- `/blockshuffle` - Opens configuration GUI<span style="color:red">*</span>
- `/blockshuffle start` - Starts the game with current settings<span style="color:red">*</span>
- `/blockshuffle end` - Ends the game<span style="color:red">*</span>
- `/blockshuffle team` - Shows current team
  - `/blockshuffle team create <teamName>` - Creates a team with the specified name
  - `/blockshuffle team remove` - Removes your current team (You must be the team captain to do this)
  - `/blockshuffle team remove <teamName>` - Removes specified team<span style="color:red">*</span>
  - `/blockshuffle team leave` - Leaves the current team
  - `/blockshuffle team color <color>` - Changes the color of the team and team members names
  - `/blockshuffle team add <player>` - Adds a player to your team without asking.<span style="color:red">*</span>
  - `/blockshuffle team invite <player>` - Invites a player to your team
  - `/blockshuffle team join <team>` - Requests to join the team
  - `/blockshuffle team accept` - Accepts an invite/join request.
  - `/blockshuffle team tp <player>` - Sends a tp request
  - `/blockshuffle team tpaccept` - Accepts a tp request
- `/blockshuffle tp <player>` - Same as team tp
- `/blockshuffle tpaccept` - Same as team tpaccept
- `/blockshuffle settings` - Shows the current options
  - `/blockshuffle settings preset <preset>` - loads a preset<span style="color:red">*</span>
  - `/blockshuffle settings load` - Loads settings from settings.yml<span style="color:red">*</span>
  - `/blockshuffle settings save` - Saves settings to settings.yml<span style="color:red">*</span>
- `/blockshuffle debug <args>` - Commands for debugging<span style="color:red">*</span>

## Configuration
Configuration can be done through the [GUI](#commands) or by editing the [settings.yml](src/main/resources/settings.yml) file: `plugins/GSBlockShuffle/settings.yml`.
### Block settings
- `difficulty: <integer>` - Current difficulty of the round
- `difficultyCap: <integer>` - Maximum value of difficulty used for incrementation
- `increaseDifficulty <true | false>` - Should difficulty be increased after each round
- `increaseEveryNRounds <integer>` - Will increase difficulty every n-th round. Set to -1 if you want to increase the difficulty at specific rounds
- `customIncrease <list of integers>` - Increment the difficulty at each round in the list
- `includeLowerDifficulties <true | false>` - Should lower difficulties be included
- `includeVariants <true | false>` - Should variants like colors and non-important derivatives (like stairs/slabs etc.) be included
- `treatAllAsIndividualBlocks <true | false>` - If true, all blocks and their variants are treated as distinct blocks with an equal chance to be chosen. (i.e. different kinds of wool)
### Round settings
- `totalRounds <integer>` - How many rounds should each game have. Set to -1 for no limit
- `roundTimeSeconds <integer>` - How long should a round last in seconds
- `roundBreakSeconds <integer>` - How long should a break between rounds last in seconds
- `blockAssignmentMode <onePerPlayer | onePerTeam | onePerGame>`
  - `onePerPlayer` - Each player gets a different block
  - `onePerTeam` - Everyone in the same team gets the same block
  - `onePerGame` - Everyone gets the same block

### Win settings
- `allPlayersRequiredForTeamWin <true | false>` - Should a team win only if every member found their block
- `teamScoreIncrementPerPlayer <true | false>` - After first player in a team finds their block, should their score still be incremented for other players
- `eliminateAfterRound <true | false>` - Should a player/team be eliminated from the game if they don't find their block
- `endGameIfOneTeamRemaining <true | false>` - Should the game finish if there is only one team remaining
- `firstToWin <true | false>` - Should a round end when first player finds their block

### Teleport settings
- `teleportMode <disabled | amountPerPlayer | amountPerTeam | unlimited>`
  - `disabled` - Don't allow teleporting between teams
  - `amountPerPlayer` - Should each player have a certain number of teleports to use
  - `amountPerTeam` - Should each team have a certain number of teleports to use
  - `unlimited` - Unlimited teleporting between team members
- `amountOfTeleportsPerRound <integer>` - How many times can a player/team teleport in each round

### Miscelanous
- `displaySplashWinnerTitle <true | false>` - Should the winner(s) get a splash title after the game ends
- `showTeamCompass <true | false>` - Should the team compass be shown for everyone
- `muteSounds <true | false>` - Should all sounds be muted
- `disablePvP <true | false>` - Should PvP be disabled

## Block list
Feel free to rearrange the `block_list_categorized.yml` file to your liking.
Make sure to follow the files structure so everything works properly.

### [block_list_categorized.yml](src/main/resources/block_list_categorized.yml) structure:
- `category`
  - `isIncluded` - Should this category be included
  - `difficulty` - Difficulty of the category
  - `elements` - This will contain all the blocks in the category
  - `base`
    - `isIncluded`
    - `difficulty`
    - `elements` - If `treatAllAsIndividualBlocks = false`, all blocks from this category will be treated as a single block in the process of randomly getting a block. If this category is chosen a block from it will be randomly selected
  - `variant`
    - `isIncluded`
    - `difficulty`
    - `elements` - these elements will be joined with the base elements if `includeVariants = true`

## Contribution
Contribution
Contributions to the development of GSBlockShuffle, including code contributions, bug reports, and questions, are welcomed. Feel free to reach out to me on Discord (`kievitz`) for any inquiries.

Developers: [stahu](https://github.com/StanislawStankiewicz), [MRcoin](https://github.com/MRcoin2)

Testers: asiulk4, c4ssp3r, Cwany, kubman, maxeloo_