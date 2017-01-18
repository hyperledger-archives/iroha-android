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

- Android Studio >=2.2.1  
- Android API Level >=v19 (v4.4, KitKat)
- Android Build Tools >=v25  

## Installation

### Gradle
In your ```app/build.gradle```   

```gradle
compile 'org.hyperledger:iroha-android:1.2.4'
```

### Maven
Or if you use Maven, like this

```maven
<dependency>
  <groupId>org.hyperledger</groupId>
  <artifactId>iroha-android</artifactId>
  <version>1.2.4</version>
  <type>pom</type>
</dependency>
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
### Initialize
In your app
on 'onCreate' your class inheriting Application

``` java
import io.soramitsu.irohaandroid.Iroha;

new Iroha.Builder()
        .baseUrl("https://[input your domain(base url)]")
        .build();
```

### Web API
#### registerAccount
```java
Iroha.getInstance().registerAccountFunction("publicKey", "alias");
// ===> return Function for register account
```

#### findAccount
```java
Iroha.getInstance().findAccount("uuid");
// ===> return Function for find account
```

#### registerDomain
```java
Iroha.getInstance().registerDomain("name", "owner", "signature");
// ===> return Function for register domain
```

#### findDomains
```java
Iroha.getInstance().findDomains(/* limit */30, /* offset */0);
// ===> return Function for find domains
```

#### createAsset
```java
Iroha.getInstance().createAsset("name", "owner", "creator", "signature");
// ===> return Function for create asset
```

#### findAssets
```java
Iroha.getInstance().findAssets("domain", /* limit */30, /* offset */0);
// ===> return Function for find assets
```

#### operationAsset
```java
Iroha.getInstance().operationAsset(
            "asset-uuid",
            "command",
            "amount",
            "sender",
            "receiver",
            "signature",
            /* timestamp */100000
);
// ===> return Function for operation
```

#### findTransactionHistory
```java
// Single asset
Iroha.getInstance().findTransactionHistory("uuid", /* limit */30, /* offset */0);
// ===> return Function for find transaction history

// Multi assets
Iroha.getInstance().findTransactionHistory("uuid", "domain", "asset-uuid",  /* limit */30, /* offset */0);
// ===> return Function for find transaction history
```

### Async
#### runAsyncTask(single task)
```java
Iroha.getInstance().runAsyncTask(
        "task's tag",
        irohaWebApiFunction,
        callback
);
```

#### runAsyncTask(multi task)
Now, you can 2 or 3 tasks in parallel.
```java
Iroha.getInstance().runParallelAsyncTask(
        activity, // Required to run on UI thread
        "task's tag1",
        irohaWebApiFunction1,
        "task's tag2",
        irohaWebApiFunction2,
        collectFunction, // collect the result of function 1 and 2
        callback
);
```

#### cancelAsyncTask
```java
Iroha.getInstance().cancelAsyncTask("task's tag");
```

### Usage example
If you use iroha web api, should call Iroha#runAsyncTask.

ex.) registerAccount
```java
Iroha iroha = Iroha.getInstance();
iroha.runAsyncTask(
        "AccountRegister",
        iroha.registerAccountFunction(keyPair.publicKey, alias),
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

ex.) findAccount and findTransactionHistory
```java
Iroha iroha = Iroha.getInstance();
iroha.runParallelAsyncTask(
        getActivity(),
        "UserInfo",
        iroha.findAccountFunction(uuid),
        "Transaction",
        iroha.findTransactionHistoryFunction(uuid, 30, 0),
        new Func2<Account, List<Transaction>, TransactionHistory>() {
            @Override
            public TransactionHistory call(Account account, List<Transaction> transactions) {
                TransactionHistory transactionHistory = new TransactionHistory();
                if (account != null && account.assets != null && !account.assets.isEmpty()) {
                    transactionHistory.value = account.assets.get(0).value;
                }
                transactionHistory.histories = transactions;
                return transactionHistory;
            }
        },
        new Callback<TransactionHistory>() {
            @Override
            public void onSuccessful(TransactionHistory result) {
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
