# iroha-android

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/122f8fc23361423e99b941b547ad95eb)](https://www.codacy.com/app/soramitsu/iroha-android?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=soramitsu/iroha-android&amp;utm_campaign=Badge_Grade)

## What's is いろは(iroha)?
いろは(iroha) is [this](https://github.com/soramitsu/iroha).

## Description
いろはAndroid(IrohaAndroid) is client library for using いろは(iroha) for Android.


- [Requirements](#requirements)
- [Installation](#installation)
- [Usage](#usage)
- [License](#license)

## Requirement
Android Studio 2.2.1
Android API Level >v19
Android Build Tools >v24

## Installation

### Gradle
In your ```app/build.gradle```

```gradle

repositories {
    maven { url 'http://hyperledger.github.com/iroha-android/repository' }
}

dependencies {
    compile 'io.soramitsu:iroha-android:1.0.0'
}
```

## Usage
### API
#### Iroha#createKeyPair
```java
KeyPair keypair = Iroha.createKeyPair();
keypair.getPublicKey(); // Ed25519 public key encoded by base64
keypair.getPrivateKey(); // Ed25519 private key encoded by base64
```
KeyPair: please check [it](https://github.com/kobaken0029/ed25519-android)

#### Iroha#sign
```java
String signature = Iroha.sign(keyPair, "MESSAGE")
//===> signature // String
```

#### Iroha#verify
```java
boolean verify = Iroha.verify(keyPair.publicKey, signature, "MESSAGE")
//===> true if the correct message
```

#### Iroha#sha3_256
```java
String hash = Iroha.sha3_256("")
//===> "a7ffc6f8bf1ed76651c14756a061d662f580ff4de43b49fa82d80a4b80f8434a"
```

#### Iroha#sha3_384
```java
String hash = Iroha.sha3_384("")
//===> "0c63a75b845e4f7d01107d852e4c2485c51a50aaaa94fc61995e71bbee983a2ac3713831264adb47fb6bd1e058d5f004"
```

#### Iroha#sha3_512
```java
String hash = Iroha.sha3_512("")
//===> "a69f73cca23a9ac5c8b567dc185a756e97c982164fe25859e0d1dcc1475c80a615b2123af1f5f94c11e3e9402c3ac558f500199d95b6d3e301758586281dcd26"
```

## Author
[kobaken0029](https://github.com/kobaken0029)

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
