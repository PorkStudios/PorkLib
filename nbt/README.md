# PorkLib NBT

[![Build Status](https://jenkins.daporkchop.net/job/PorkLib/job/nbt/badge/icon)](https://jenkins.daporkchop.net/job/PorkLib/job/nbt)
[![Discord](https://img.shields.io/discord/428813657816956929.svg)](https://discord.gg/FrBHHCk)

A fast and easy-to-use library for interacting with NBT (Named Binary Tag) data.

See Notch's official specification of the format [here](http://web.archive.org/web/20110723210920/http://www.minecraft.net/docs/NBT.txt)

This library has some additional features for projects that don't have to follow the official NBT schema (i.e. interact with Minecraft itself):

- Array tags for
  - `short`
  - `long`
  - `float`
  - `double`
  - `java.lang.String`
- Custom Object serializers

## Usage

### IO

- **Reading**  
 Read a compound tag from a file:  
 `CompundTag tag = NBTIO.read(new File(".", "test.nbt"));`  
 Read a compound tag from a byte array:  
 `CompundTag tag = NBTIO.read(new byte[0]);`  
 Read a compound tag from an input stream:  
 `CompundTag tag = NBTIO.read(stream);`  
- **Writing**  
 Write a compound tag to a file:  
 `NBTIO.write(tag, new File(".", "test2.nbt"));`  
 Write a compound tag to a byte array:  
 `byte[] bytes = NBTIO.write(tag);`  
 Write a compound tag to an output stream:  
 `NBTIO.write(tag, stream);`

### Compression

PorkLib NBT supports GZIP and ZLib compression.

- **Writing**  
  - **GZIP**  
 Write a GZIP-compressed tag to an output stream:  
 `NBTIO.writeGZIPCompressed(tag, stream);`  
 Write a GZIP-compressed tag to a byte array:  
 `byte[] bytes = NBTIO.writeGZIPCompressed(tag);`

  - **ZLib**  
 Write a ZLib-compressed tag to an output stream:  
 `NBTIO.writeZLIBCompressed(tag, stream);`  
 Write a ZLib-compressed tag to a byte array:  
 `byte[] bytes = NBTIO.writeZLIBCompressed(tag);`  
- **Reading**  
 To read a tag compressed using either ZLib or GZIP, use:  
 `CompoundTag tag = NBTIO.readCompressed(bytes);`  
 or  
 `CompoundTag tag = NBTIO.readCompressed(stream);`

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
    <artifactId>nbt</artifactId>
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
compile 'net.daporkchop.lib:nbt:0.0.1'
```
