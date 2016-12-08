package io.soramitsu.irohaandroid.entity.mapper;

import java.util.ArrayList;
import java.util.List;

import io.soramitsu.irohaandroid.entity.TransactionEntity;
import io.soramitsu.irohaandroid.entity.TransactionListEntity;
import io.soramitsu.irohaandroid.model.Transaction;

import static io.soramitsu.irohaandroid.model.Transaction.OperationParameter;

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
            transaction.timestamp = transactionEntity.timestamp;
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
            }

            return operationParameter;
        }
    }
}
