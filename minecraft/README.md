# PorkLib Minecraft

[![Build Status](https://jenkins.daporkchop.net/job/PorkLib/job/minecraft/badge/icon)](https://jenkins.daporkchop.net/job/PorkLib/job/minecraft)
[![Discord](https://img.shields.io/discord/428813657816956929.svg)](https://discord.gg/FrBHHCk)

Makes interacting with the Minecraft network protocols less of a pain

### Dependency management

#### Maven

Add the repository:

```xml
<repository>
    <id>DaPorkchop_</id>
    <url>https://maven.daporkchop.net/</url>
</repository>
```

Dependency:

```xml
<dependency>
    <groupId>net.daporkchop.lib</groupId>
    <artifactId>minecraft</artifactId>
    <version>0.0.1</version>
</dependency>
```

#### Gradle

Add the repository:

```groovy
maven { 
    url 'https://maven.daporkchop.net/'
}
```

Dependency:

```groovy
compile 'net.daporkchop.lib:minecraft:0.0.1'
```
