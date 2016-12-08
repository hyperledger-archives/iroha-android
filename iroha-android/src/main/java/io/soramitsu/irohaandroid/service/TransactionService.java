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

    public List<Transaction> findHistory(String uuid)
            throws IOException, HttpBadRequestException {
        return transactionEntityDataMapper.transform(transactionRepository.findHistory(uuid));
    }

    public List<Transaction> findHistory(String uuid, String domain, String asset)
            throws IOException, HttpBadRequestException {
        return transactionEntityDataMapper.transform(transactionRepository.findHistory(uuid, domain, asset));
    }
}
