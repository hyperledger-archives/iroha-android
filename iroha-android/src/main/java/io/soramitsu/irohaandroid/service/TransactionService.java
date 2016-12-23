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

package io.soramitsu.irohaandroid.service;

import java.io.IOException;
import java.util.List;

import io.soramitsu.irohaandroid.entity.mapper.TransactionEntityDataMapper;
import io.soramitsu.irohaandroid.exception.HttpBadRequestException;
import io.soramitsu.irohaandroid.model.Transaction;
import io.soramitsu.irohaandroid.repository.TransactionRepository;
import io.soramitsu.irohaandroid.repository.impl.TransactionRepositoryImpl;

public class TransactionService {

    private final TransactionRepository transactionRepository = new TransactionRepositoryImpl();
    private final TransactionEntityDataMapper transactionEntityDataMapper = new TransactionEntityDataMapper();

    public List<Transaction> findHistory(String uuid, int limit, int offset)
            throws IOException, HttpBadRequestException {
        return transactionEntityDataMapper.transform(transactionRepository.findHistory(uuid, limit, offset));
    }

    public List<Transaction> findHistory(String uuid, String domain, String asset, int limit, int offset)
            throws IOException, HttpBadRequestException {
        return transactionEntityDataMapper.transform(transactionRepository.findHistory(uuid, domain, asset, limit, offset));
    }
}
