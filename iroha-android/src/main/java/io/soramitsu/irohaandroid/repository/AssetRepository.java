package io.soramitsu.irohaandroid.repository;

import java.io.IOException;

import io.soramitsu.irohaandroid.entity.AssetEntity;
import io.soramitsu.irohaandroid.entity.AssetListEntity;
import io.soramitsu.irohaandroid.exception.HttpBadRequestException;
import io.soramitsu.irohaandroid.net.dataset.reqest.AssetOperationRequest;
import io.soramitsu.irohaandroid.net.dataset.reqest.AssetRegisterRequest;

public interface AssetRepository {
    AssetEntity create(AssetRegisterRequest body)
            throws IOException, HttpBadRequestException;

    AssetListEntity findAssets(String domain)
            throws IOException, HttpBadRequestException;

    boolean operation(AssetOperationRequest body)
            throws IOException, HttpBadRequestException;
}
