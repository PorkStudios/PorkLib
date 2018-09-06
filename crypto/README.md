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

### Usage

- Key generation  
All you need to do is find the `KeyGen` class corresponding to the algorithm you want to generate keys for and invoke `gen` (with parameters, if required, and an optional seed. If no seed is given, a random seed will be chosen).  
Examples:
  - `AESKey aesKey = AESKeyGen.gen(); //random seed will be used`
  - `Threefish_512Key threefishKey = Threefish_512KeyGen.gen(new byte[]{0,0,0,0,0}); //manually specified seed`
  - `ECDSAKeyPair ecdsaKeys = ECDSAKeyGen.gen(ECCurves.c2pnb368w1); //specify curve type`
  - `RSAKeyPair rsaKeys = RSAKeyGen.gen(4096); //key size (bits)`
- Encryption
  - Symmetric  
  Find the helper class for your chosen symmetric algorithm and create a new instance of it.  The `encrypt` and `decrypt` methods should be pretty self-explanatory.  
  Examples:
    - `AESHelper aesHelper = new AESHelper(BlockCipherMode.CFB, PaddingScheme.PKCS7, aesKey);`  
    `byte[] aesEncrypted = aesHelper.encrypt("hello world!".getBytes());`  
    `System.out.println(new String(aesHelper.decrypt(aesEncrypted))); //prints "hello world!"`
    - `Threefish_512Helper threefishHelper = new Threefish_512Helper(BlockCipherMode.OFB, PaddingScheme.ZERO_BYTE, threefishKey);`  
    `byte[] threefishEncrypted = threefishHelper.encrypt("threefish works too".getBytes());`  
    `System.out.println(new String(threefishHelper.decrypt(threefishEncrypted))); //prints "threefish works too"`
  - Asymmetric  
  Works just like symmetric encryption. For RSA, the input data cannot be longer than the size of the key (size is defined in the call to RSAKeyGen.gen())  
  Example:
    - `RSACipherHelper rsaHelper = new RSACipherHelper();`  
    `byte[] rsaEncrypted = rsaHelper.encrypt("rsa is very secure".getBytes(), rsaKey);`  
    `System.out.println(new String(rsaHelper.decrypt(rsaEncrypted, rsaKey))); //prints "rsa is very secure"`
- Signatures  
These work nearly the same as encryption, just different names.  
Examples:  
  - `ECDSAHelper ecdsaHelper = new ECDSAHelper(HashTypes.SHA_256);`  
  `byte[] ecdsaSig = ecdsaHelper.sign("random bytes".getBytes(), ecdsaKeys);`  
  `System.out.println(ecdsaHelper.verify(ecdsaSig, "random bytes".getBytes(), ecdsaKeys)); //prints "true"`
  - `RSASignatureHelper rsaSigHelper = new RSASignatureHelper(HashTypes.WHIRLPOOL);`  
  `byte[] rsaSig = rsaSigHelper.sign("rsa can sign too".getBytes(), rsaKeys);`  
  `System.out.println(rsaSigHelper.verify(rsaSig, "rsa can sign too".getBytes(), rsaKeys)); //prints "true"`

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
    <artifactId>crypto</artifactId>
    <version>0.1.4</version>
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
compile 'net.daporkchop.lib:crypto:0.1.4'
```
