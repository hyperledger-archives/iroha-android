package io.soramitsu.irohaandroid.service;

import java.io.IOException;
import java.util.List;

import io.soramitsu.irohaandroid.entity.mapper.AssetEntityDataMapper;
import io.soramitsu.irohaandroid.exception.HttpBadRequestException;
import io.soramitsu.irohaandroid.model.Asset;
import io.soramitsu.irohaandroid.model.Transaction;
import io.soramitsu.irohaandroid.net.dataset.reqest.AssetOperationRequest;
import io.soramitsu.irohaandroid.net.dataset.reqest.AssetRegisterRequest;
import io.soramitsu.irohaandroid.repository.AssetRepository;
import io.soramitsu.irohaandroid.repository.impl.AssetRepositoryImpl;

public class AssetService {

    private final AssetRepository assetRepository = new AssetRepositoryImpl();
    private final AssetEntityDataMapper assetEntityDataMapper = new AssetEntityDataMapper();

    public Asset create(String name, String domain, String creator, String signature)
            throws IOException, HttpBadRequestException {

        final AssetRegisterRequest body = new AssetRegisterRequest();
        body.name = name;
        body.domain = domain;
        body.creator = creator;
        body.timestamp = System.currentTimeMillis() / 1000;
        body.signature = signature;

        return assetEntityDataMapper.transform(assetRepository.create(body));
    }

    public List<Asset> findAssets(String domain)
            throws IOException, HttpBadRequestException {
        return assetEntityDataMapper.transform(assetRepository.findAssets(domain));
    }

    public boolean operation(String assetUuid, String command, String value,
                             String sender, String receiver, String signature)
            throws IOException, HttpBadRequestException {

        final AssetOperationRequest body = new AssetOperationRequest();
        body.uuid = assetUuid;
        body.params = new Transaction.OperationParameter();
        body.params.command = command;
        body.params.value = value;
        body.params.sender = sender;
        body.params.receiver = receiver;
        body.signature = signature;
        body.timestamp = System.currentTimeMillis() / 1000;

        return assetRepository.operation(body);
    }
}
