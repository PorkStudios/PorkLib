# PorkLib Crypto

[![Build Status](https://jenkins.daporkchop.net/job/PorkLib/job/crypto/badge/icon)](https://jenkins.daporkchop.net/job/PorkLib/job/crypto)
[![Discord](https://img.shields.io/discord/428813657816956929.svg)](https://discord.gg/FrBHHCk)

A wrapper for the [BouncyCastle](https://www.bouncycastle.org/java.html) cryptography library. Designed to make Java cryptography less of a pain to work with by providing simple methods to do things, rather than complex messes of try-catch blocks and block cyphers.

### Supported algorithms  
- Block ciphers (symmetric encryption)  
  - AES
  - ARIA
  - Blowfish
  - Camellia
  - CAST5
  - CAST6
  - DES
  - DSTU-7624
    - 128-bit
    - 256-bit
    - 512-bit
  - GOST
    - 3412-2015
    - 28147
  - IDEA
  - Noekeon
  - ***PorkCrypt2***
  - RC2
  - RC6
  - Rijndael
  - SEED
  - Serpent
  - Shacal2
  - SKIPJACK
  - SM4
  - TEA
  - Threefish
    - 256-bit
    - 512-bit
    - 1024-bit
  - Twofish
  - XTEA
- Asymmetric
  - RSA
- Signatures
  - ECDSA
  - RSA
- Key exchange  
  - Diffie-Hellman
  - ECDH
