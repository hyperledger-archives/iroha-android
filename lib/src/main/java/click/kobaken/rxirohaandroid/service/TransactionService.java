/*
Copyright Soramitsu Co., Ltd. 2016 All Rights Reserved.
http://soramitsu.co.jp

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package click.kobaken.rxirohaandroid.service;

import click.kobaken.rxirohaandroid.model.TransactionHistory;
import click.kobaken.rxirohaandroid.repository.TransactionRepository;
import io.reactivex.Observable;


public class TransactionService {
    private TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Observable<TransactionHistory> findHistory(String uuid, int limit, int offset) {
        return transactionRepository.findHistory(uuid, limit, offset);
    }

    public Observable<TransactionHistory> findHistory(
            String domain, String asset, String uuid, int limit, int offset) {
        return transactionRepository.findHistory(domain, asset, uuid, limit, offset);
    }
}
