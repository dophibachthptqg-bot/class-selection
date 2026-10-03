# RPG Series Class Selector

Native NeoForge class selector for Minecraft 1.21.1.

## Target
- Minecraft 1.21.1
- NeoForge 21.1.251+
- Java 21
- Spell Engine 1.10+ (required at runtime)
- RPG Series / RPG Series Plus class mods are optional and detected through each class entry's `required_mods`.

## Features
- First-login class selection GUI
- Server-authoritative selection
- Persistent player class
- JSON-configurable classes
- Optional-mod gating
- Spell book/item grants
- Scoreboard tags
- Commands on class selection/removal
- `/class`, `/class open`, `/class reload`, and admin `/class reset`

The class config is generated at:
`config/rpg_class_selector/classes.json`

Edit `required_mods` to match the exact mod IDs in your pack. Classes whose required mods are missing are not sent to clients.
