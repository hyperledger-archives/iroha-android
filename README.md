Iroha Android Sample
=====================

The App shows example of usage [Iroha Java](https://github.com/hyperledger/iroha-java) for Android development. 

## Funcionality

* Creating account

* Adding assets

* Transfering assets

* Querying balance

* Querying transaction history

* Handing errors while sending transactions

## How to run

* Clone current repository.

* Change admin keypair in jp.co.soramitsu.irohaandroid.util.Const.

* Change admin and test keypair in genesis.block.

* Follow instructions from [Iroha documentation](https://iroha.readthedocs.io/en/latest/getting_started/) to setup and run iroha peer in Docker container using our genesis.block.

* Change IP address of Iroha peer in jp.co.soramitsu.irohaandroid.di.AppModule

* Run app
    
## Authors
[Ali Abdulmadzhidov](https://github.com/mrZizik)

## License

Copyright 2018 Soramitsu Co., Ltd.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
