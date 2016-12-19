package io.soramitsu.irohaandroid.repository;

import java.io.IOException;

import io.soramitsu.irohaandroid.entity.TransactionListEntity;
import io.soramitsu.irohaandroid.exception.HttpBadRequestException;

public interface TransactionRepository {
    TransactionListEntity findHistory(String uuid, int limit, int offset)
            throws IOException, HttpBadRequestException;

    TransactionListEntity findHistory(String uuid, String domain, String asset, int limit, int offset)
            throws IOException, HttpBadRequestException;
}
