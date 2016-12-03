package io.soramitsu.irohaandroid.data.repository;

import android.content.Context;

import java.util.List;

import io.soramitsu.irohaandroid.data.cache.FileManager;
import io.soramitsu.irohaandroid.data.cache.TransactionListCache;
import io.soramitsu.irohaandroid.data.cache.TransactionListCacheImpl;
import io.soramitsu.irohaandroid.data.cache.serializer.JsonSerializer;
import io.soramitsu.irohaandroid.data.entity.TransactionListEntity;
import io.soramitsu.irohaandroid.data.entity.mapper.TransactionEntityDataMapper;
import io.soramitsu.irohaandroid.data.executor.JobExecutor;
import io.soramitsu.irohaandroid.data.repository.datasource.transaction.TransactionDataFactory;
import io.soramitsu.irohaandroid.data.repository.datasource.transaction.TransactionDataStore;
import io.soramitsu.irohaandroid.domain.entity.Transaction;
import io.soramitsu.irohaandroid.domain.repository.TransactionRepository;
import rx.Observable;
import rx.functions.Func1;

public class TransactionDataRepository implements TransactionRepository {

    private final Context context;
    private final TransactionDataFactory transactionDataFactory;
    private final TransactionEntityDataMapper transactionEntityDataMapper;

    public TransactionDataRepository(Context context) {
        this.context = context;

        TransactionListCache transactionListCache = new TransactionListCacheImpl(
                this.context,
                new JsonSerializer(),
                new FileManager(),
                new JobExecutor()
        );
        this.transactionDataFactory = new TransactionDataFactory(transactionListCache);
        this.transactionEntityDataMapper = new TransactionEntityDataMapper();
    }

    @Override
    public Observable<List<Transaction>> history(String uuid) {
        TransactionDataStore transactionDataStore = transactionDataFactory.create(uuid);
        return transactionDataStore.history(uuid).map(new Func1<TransactionListEntity, List<Transaction>>() {
            @Override
            public List<Transaction> call(TransactionListEntity transactionListEntity) {
                return transactionEntityDataMapper.transform(transactionListEntity);
            }
        });
    }

    @Override
    public Observable<List<Transaction>> history(String uuid, String domain, String asset) {
        TransactionDataStore transactionDataStore = transactionDataFactory.create(uuid, domain, asset);
        return transactionDataStore.history(uuid, domain, asset).map(new Func1<TransactionListEntity, List<Transaction>>() {
            @Override
            public List<Transaction> call(TransactionListEntity transactionListEntity) {
                return transactionEntityDataMapper.transform(transactionListEntity);
            }
        });
    }
}
