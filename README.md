# Essential-ServerFeature

![GitHub release](https://img.shields.io/github/v/release/Team-Jackdaw/Essential-ServerFeature?include_prereleases)
![GitHub license](https://img.shields.io/github/license/Team-Jackdaw/Essential-ServerFeature)
![test workflow](https://github.com/Team-Jackdaw/Essential-ServerFeature/actions/workflows/build.yml/badge.svg)

## 1. Introduction

This is a basic feature for fabric server. It includes the following features:

- **Game mode switching**: The game mode can be switched quickly by using the command `/c` or `/s` representing game mode `spectator`, `survival`, respectively.

- **Basic player teleport**: The player can use the command below to set, delete, or teleport to a specific location.

  - /tel set \<name> - set a point
  - /tel del \<name> - delete a point 
  - /tel to \<name> - teleport to a point

- **Give OP**: The player can use the command `/god` to give OP permission to themselves.

  This plugin can also make all OPs into temporary permissions, i.e. players will have their op permissions removed when they are offline.

## 2. Installation

1. Download the plugin from the release page.
2. Put the plugin into the `mods` folder of your server.
3. Restart the server.

## 3. Usage

1. Use the command `/c` or `/s` to switch game mode.
2. Use the command `/tel set <name>`, `/tel del <name>`, or `/tel to <name>` to set, delete, or teleport to a specific location.
3. Use the command `/god` to give OP permission to yourself.

## 4. Configuration

The configuration file is located at `config/essential-server-feature/config.toml`. You can modify the configuration file to change the behavior of the plugin. The default configuration is as follows:

```toml
[version]
version = "v1.1"

[fastGameModeSetting]
enabled=true

[teleport]
enabled=true

[giveOP]
enabled=true
temp=true
```

## 5. Build

1. Clone the repository.
2. Run `./gradlew build` in the root directory of the repository.
3. The jar file will be generated in the `build/libs` directory.
