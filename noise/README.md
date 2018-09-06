# PorkLib Noise

[![Build Status](https://jenkins.daporkchop.net/job/PorkLib/job/noise/badge/icon)](https://jenkins.daporkchop.net/job/PorkLib/job/noise)
[![Discord](https://img.shields.io/discord/428813657816956929.svg)](https://discord.gg/FrBHHCk)

A fast, powerful and easy-to-use noise library for Java.

## Usage

Generating raw noise:

```java
long seed = 123456789L; //can be anything
INoiseEngine engine = new PerlinEngine(seed); //can be replaced by any engine class
engine = NoiseEngineType.PERLIN.getEngine(seed); //this works too!
double noise = engine.get(0.0d, 0.0d, 0.0d, 0.0d); //the number of arguments specifies the number of dimensions (up to 4)
```

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
    <artifactId>noise</artifactId>
    <version>0.0.3</version>
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
compile 'net.daporkchop.lib:noise:0.0.3'
```
