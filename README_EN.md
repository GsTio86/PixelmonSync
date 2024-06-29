# PixelmonSync

A mod that syncs Pixelmon player data across servers.

[English](./README.md) | [繁體中文](./README_ZH_TW.md)

## Overview

PixelmonSync is designed to synchronize player data across multiple Pixelmon servers. It utilizes MySQL to store player data, ensuring consistency across servers. Data operations occur only when a player logs in, logs out, or executes the save command.

## Compatibility

Currently supports Pixelmon 1.16.5 9.1.11 and Java 11+.

## Installation

1. Place the `.jar` file into the `mods` folder.
2. Start the server and then stop it. This will generate a `pixelmonsync-common.toml` file in the `config` directory.
3. Configure the MySQL information in the `pixelmonsync-common.toml`.
4. Restart the server.

Enjoy seamless data synchronization across your Pixelmon servers!
