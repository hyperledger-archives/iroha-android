# iroha-android

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
Android Studio >=2.2.1  
Android API Level >=v19  
Android Build Tools >=v25  

## Installation

### Gradle
In your ```app/build.gradle```

```gradle
repositories {
    maven { url 'http://hyperledger.github.com/iroha-android/repository' }
}

dependencies {
    compile 'io.soramitsu:iroha-android:1.0'
}
```

## Usage
### API

### io.soramitsu.irohaandroid.security.KeyGenerator
#### createKeyPair
```java
import io.soramitsu.irohaandroid.security.KeyGenerator;
import io.soramitsu.irohaandroid.model.KeyPair;

KeyPair keypair = KeyGenerator.createKeyPair();
keypair.publicKey; // Ed25519 public key encoded by base64
keypair.privateKey; // Ed25519 private key encoded by base64
```

#### sign
```java
import io.soramitsu.irohaandroid.security.KeyGenerator;
import io.soramitsu.irohaandroid.model.KeyPair;

String signature = KeyGenerator.sign(keyPair, "message")
//===> signature // String
```

#### verify
```java
import io.soramitsu.irohaandroid.security.KeyGenerator;
import io.soramitsu.irohaandroid.model.KeyPair;

boolean verify = Iroha.verify(keyPair.publicKey, signature, "message")
//===> true if the correct message
```

### io.soramitsu.irohaandroid.security.MessageDigest
#### digest
```java
String hashedMessage = MessageDigest.digest("message", MessageDigest.SHA3_256);
// ===> hashed message by SHA3_256
```

### io.soramitsu.irohaandroid.Iroha
#### Initialize
In your app
on 'onCreate' your class inheriting Application

``` java
import io.soramitsu.irohaandroid.Iroha;

new Iroha.Builder()
        .baseUrl("https://[input your domain(base url)]")
        .build();
```

#### registerAccount
```java
import io.soramitsu.irohaandroid.Iroha;
import io.soramitsu.irohaandroid.model.Account;

Iroha.getInstance().registerAccount("publicKey", "alias",
         new Callback<Account>() {
             @Override
             public void onSuccessful(Account result) {
                 // Success!
             }

             @Override
             public void onFailure(Throwable throwable) {
                 // Error!
             }
         }
 );
```

#### findAccount
```java
import io.soramitsu.irohaandroid.Iroha;
import io.soramitsu.irohaandroid.model.Account;

Iroha.getInstance().findAccount("uuid",
         new Callback<Account>() {
             @Override
             public void onSuccessful(Account result) {
                 // Success!
             }

             @Override
             public void onFailure(Throwable throwable) {
                 // Error!
             }
         }
 );
```

#### registerDomain
```java
import io.soramitsu.irohaandroid.Iroha;
import io.soramitsu.irohaandroid.model.Domain;

Iroha.getInstance().registerDomain("name", "owner", "signature",
         new Callback<Domain>() {
             @Override
             public void onSuccessful(Domain result) {
                 // Success!
             }

             @Override
             public void onFailure(Throwable throwable) {
                 // Error!
             }
         }
 );
```

#### findDomains
```java
import io.soramitsu.irohaandroid.Iroha;
import io.soramitsu.irohaandroid.model.Domain;

Iroha.getInstance().findDomains(/* limit */30, /* offset */0,
         new Callback<List<Domain>>() {
             @Override
             public void onSuccessful(List<Domain> result) {
                 // Success!
             }

             @Override
             public void onFailure(Throwable throwable) {
                 // Error!
             }
         }
 );
```

#### createAsset
```java
import io.soramitsu.irohaandroid.Iroha;
import io.soramitsu.irohaandroid.model.Asset;

Iroha.getInstance().createAsset("name", "owner", "creator", "signature",
         new Callback<Asset>() {
             @Override
             public void onSuccessful(Asset result) {
                 // Success!
             }

             @Override
             public void onFailure(Throwable throwable) {
                 // Error!
             }
         }
 );
```

#### findAssets
```java
import io.soramitsu.irohaandroid.Iroha;
import io.soramitsu.irohaandroid.model.Asset;

Iroha.getInstance().findAssets("domain", /* limit */30, /* offset */0,
         new Callback<List<Asset>>() {
             @Override
             public void onSuccessful(List<Asset> result) {
                 // Success!
             }

             @Override
             public void onFailure(Throwable throwable) {
                 // Error!
             }
         }
 );
```

#### operationAsset
```java
import io.soramitsu.irohaandroid.Iroha;
import io.soramitsu.irohaandroid.model.Asset;

Iroha.getInstance().operationAsset("asset-uuid", "command", "amount", "sender", "receiver", "signature",
         new Callback<Boolean>() {
             @Override
             public void onSuccessful(Boolean result) {
                 // Success!
             }

             @Override
             public void onFailure(Throwable throwable) {
                 // Error!
             }
         }
 );
```

#### findTransactionHistory
```java
import io.soramitsu.irohaandroid.Iroha;
import io.soramitsu.irohaandroid.model.Transaction;

// Single asset
Iroha.getInstance().findTransactionHistory("uuid", /* limit */30, /* offset */0,
         new Callback<List<Transaction>>() {
             @Override
             public void onSuccessful(List<Transaction> result) {
                 // Success!
             }

             @Override
             public void onFailure(Throwable throwable) {
                 // Error!
             }
         }
 );

// Multi assets
Iroha.getInstance().findTransactionHistory("uuid", "domain", "asset-uuid",  /* limit */30, /* offset */0,
         new Callback<List<Transaction>>() {
             @Override
             public void onSuccessful(List<Transaction> result) {
                 // Success!
             }

             @Override
             public void onFailure(Throwable throwable) {
                 // Error!
             }
         }
 );
```


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
