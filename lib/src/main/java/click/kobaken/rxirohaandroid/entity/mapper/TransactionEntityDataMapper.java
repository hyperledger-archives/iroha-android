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

package click.kobaken.rxirohaandroid.entity.mapper;

import java.util.ArrayList;
import java.util.List;

import click.kobaken.rxirohaandroid.entity.TransactionEntity;
import click.kobaken.rxirohaandroid.entity.TransactionListEntity;
import click.kobaken.rxirohaandroid.model.Transaction;

import static click.kobaken.rxirohaandroid.model.Transaction.OperationParameter;

public class TransactionEntityDataMapper {

    private OperationEntityDataMapper operationEntityDataMapper;

    public TransactionEntityDataMapper() {
        this.operationEntityDataMapper = new OperationEntityDataMapper();
    }

    public Transaction transform(TransactionEntity transactionEntity) {
        Transaction transaction = null;

        if (transactionEntity != null) {
            transaction = new Transaction();
            transaction.assetUuid = transactionEntity.assetUuid;
            transaction.assetName = transactionEntity.assetName;
            transaction.params = operationEntityDataMapper.transform(transactionEntity.params);
            transaction.signature = transactionEntity.signature;
        }

        return transaction;
    }

    public List<Transaction> transform(TransactionListEntity transactionListEntity) {
        List<Transaction> transactions = new ArrayList<>();
        for (TransactionEntity transactionEntity : transactionListEntity.transactionEntities) {
            transactions.add(transform(transactionEntity));
        }
        return transactions;
    }

    private static class OperationEntityDataMapper {
        public OperationParameter transform(TransactionEntity.OperationParameterEntity operationParameterEntity) {
            OperationParameter operationParameter = null;

            if (operationParameterEntity != null) {
                operationParameter = new OperationParameter();
                operationParameter.command = operationParameterEntity.command;
                operationParameter.value = operationParameterEntity.value;
                operationParameter.sender = operationParameterEntity.sender;
                operationParameter.receiver = operationParameterEntity.receiver;
                operationParameter.oppoent = operationParameterEntity.opponent;
                operationParameter.timestamp = operationParameterEntity.timestamp;
            }

            return operationParameter;
        }
    }
}
