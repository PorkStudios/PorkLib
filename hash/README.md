# PorkLib Hashing

[![Build Status](https://jenkins.daporkchop.net/job/PorkLib/job/hash/badge/icon)](https://jenkins.daporkchop.net/job/PorkLib/job/hash)
[![Discord](https://img.shields.io/discord/428813657816956929.svg)](https://discord.gg/FrBHHCk)

Implements a number of cryptographically secure hashing algorithms, in a way to make everthing a bit less repetitive repetitive repetitive repetitive

Supported algorithms:

- Haval
- MD2
- MD4
- MD5
- PorkHash
- RIPEMD128
- RIPEMD160
- Tiger
- SHA-160
- SHA-256
- SHA-384
- SHA-512
- Whirlpool

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
    <artifactId>hash</artifactId>
    <version>0.0.5</version>
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
compile 'net.daporkchop.lib:hash:0.0.5'
```
