# HitBox+

[![Modrinth Downloads](https://img.shields.io/modrinth/dt/hitboxplus?logo=modrinth&label=downloads)](https://modrinth.com/mod/hitboxplus)
[![Discord](https://img.shields.io/badge/Discord-join-5865F2?logo=discord&logoColor=white)](https://discord.gg/Jm2gPbvZZa)
[![License: MIT](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

HitBox+ is a client-side Fabric mod for customizing Minecraft entity hitbox colors.

## Screenshots

Player rules let you tune self, neutral, friend, and enemy hitboxes separately.

![Player hitbox configuration](assets/players.png)

Entity overrides let you enable custom styling for individual entity ids.

![Entity hitbox overrides](assets/entities.png)

Group defaults cover broad categories such as passive mobs, hostile mobs, bosses, projectiles, vehicles, and miscellaneous entities.

![Group hitbox defaults](assets/groups.png)

The style editor controls color, visibility, line thickness, pattern, eye line, and look direction for each rule.

![Hitbox style editor](assets/hitbox_config.png)

## Features

- Customize the default hitbox style used for entities without a more specific rule.
- Configure separate styles for self, neutral players, friends, and enemies.
- Use Minecraft team colors for neutral players when enabled.
- Set broad defaults for passive mobs, hostile mobs, bosses, projectiles, effects, vehicles, and miscellaneous entities.
- Add per-entity overrides for any registered entity id.
- Choose full or dotted hitbox lines, line thickness, eye line visibility, and look direction visibility.

## Configuration

HitBox+ uses YACL for its config screen and integrates with Mod Menu when it is installed. The config file is stored at:

```text
config/hitboxplus.json5
```

The config screen includes active entity overrides, an entity picker, and a custom entity id field for ids such as `minecraft:zombie`.

## Commands

HitBox+ adds client-side commands for managing player relation lists:

```text
/hitboxplus friend add <player>
/hitboxplus friend remove <player>
/hitboxplus friend list
/hitboxplus enemy add <player>
/hitboxplus enemy remove <player>
/hitboxplus enemy list
```

Player name suggestions are provided from online players and existing configured names.

## Quick Player Tagging

Use Minecraft's pick-block control while targeting a player to cycle that player through:

```text
friend -> enemy -> neutral
```

HitBox+ saves the change immediately and shows a chat confirmation.

## Requirements

- Minecraft `26.1`
- Fabric Loader `>=0.18.5`
- Fabric API
- YetAnotherConfigLib `~3.9`
- Mod Menu is recommended for opening the config screen from the mods list.

## Downloads

Download HitBox+ from Modrinth:

https://modrinth.com/mod/hitboxplus

Join the Discord:

https://discord.gg/Jm2gPbvZZa

## Development

Development notes are in [DEVELOPMENT.md](DEVELOPMENT.md).

## License

HitBox+ is licensed under the [MIT License](LICENSE).
