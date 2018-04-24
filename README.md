# iroha-android

![build status](https://circleci.com/gh/hyperledger/iroha-android.svg?style=shield&circle-token=55ddf2685110d2e067b5a361ccc5743f9e7d1df9)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/122f8fc23361423e99b941b547ad95eb)](https://www.codacy.com/app/hyperledger/iroha-android?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=hyperledger/iroha-android&amp;utm_campaign=Badge_Grade)

## What is いろは(iroha)?
いろは(iroha) is [this](https://github.com/hyperledger/iroha).

## Description
いろはAndroid(IrohaAndroid) is client library for using いろは(iroha) for Android.


- [Requirements](#requirements)
- [Installation](#installation)
- [Usage](#usage)
- [License](#license)

## Requirement

- Android Studio >=2.2.1  
- Android API Level >=v19 (v4.4, KitKat)
- Android Build Tools >=v25  

## Installation

### Gradle
In your ```app/build.gradle```   

```gradle
compile 'org.hyperledger:iroha-android:1.4.0'
```

### Maven
Or if you use Maven, like this

```maven
<dependency>
  <groupId>org.hyperledger</groupId>
  <artifactId>iroha-android</artifactId>
  <version>1.4.0</version>
  <type>pom</type>
</dependency>
```


## Usage
### API

### io.soramitsu.irohaandroid.security.MessageDigest
#### digest
```java
String hashedMessage = MessageDigest.digest("message", MessageDigest.SHA3_256);
// ===> hashed message by SHA3_256
```

### io.soramitsu.irohaandroid.Iroha
#### createKeyPair
```java
import io.soramitsu.irohaandroid.Iroha;
import io.soramitsu.irohaandroid.model.KeyPair;

KeyPair keypair = Iroha.createKeyPair();
keypair.publicKey; // Ed25519 public key encoded by base64
keypair.privateKey; // Ed25519 private key encoded by base64
```

#### sign
```java
import io.soramitsu.irohaandroid.Iroha;
import io.soramitsu.irohaandroid.model.KeyPair;

String signature = Iroha.sign(keyPair, "message")
//===> signature // String
```

#### verify
```java
import io.soramitsu.irohaandroid.Iroha;
import io.soramitsu.irohaandroid.model.KeyPair;

boolean verify = Iroha.verify(keyPair.publicKey, signature, "message")
//===> true if the correct message
```

### Web API
**Sorry, This API has been removed.**

## Author
[kobaken0029](https://github.com/kobaken0029)

[http://soramitsu.co.jp](http://soramitsu.co.jp)

## License

Copyright 2016 Soramitsu Co., Ltd.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
