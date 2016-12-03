package io.soramitsu.irohaandroid.data.entity.mapper;

import java.util.List;

import io.soramitsu.irohaandroid.data.entity.TransactionEntity;
import io.soramitsu.irohaandroid.data.entity.TransactionListEntity;
import io.soramitsu.irohaandroid.domain.entity.OperationParameter;
import io.soramitsu.irohaandroid.domain.entity.Transaction;
import rx.Observable;
import rx.functions.Func1;

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
        Func1<TransactionEntity, Transaction> convertAction = new Func1<TransactionEntity, Transaction>() {
            @Override
            public Transaction call(TransactionEntity domainEntity) {
                return transform(domainEntity);
            }
        };
        return Observable.from(transactionListEntity.transactionEntities)
                .map(convertAction)
                .toList()
                .toBlocking()
                .single();
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
