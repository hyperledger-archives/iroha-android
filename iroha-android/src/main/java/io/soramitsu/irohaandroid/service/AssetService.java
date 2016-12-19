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
import io.soramitsu.irohaandroid.security.MessageDigest;

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

    public List<Asset> findAssets(String domain, int limit, int offset)
            throws IOException, HttpBadRequestException {
        return assetEntityDataMapper.transform(assetRepository.findAssets(domain, limit, offset));
    }

    public boolean operation(String assetUuid, String command, String value,
                             String sender, String receiver)
            throws IOException, HttpBadRequestException {

        final AssetOperationRequest body = new AssetOperationRequest();
        body.uuid = assetUuid;
        body.params = new Transaction.OperationParameter();
        body.params.command = command;
        body.params.value = value;
        body.params.sender = sender;
        body.params.receiver = receiver;
        body.timestamp = System.currentTimeMillis() / 1000;
        body.signature = MessageDigest.digest(generateMessage(body), MessageDigest.Algorithm.SHA3_256);

        return assetRepository.operation(body);
    }

    private String generateMessage(AssetOperationRequest body) {
        Transaction.OperationParameter params = body.params;
        return "timestamp:" + body.timestamp
                + ",value:" + params.value
                + ",sender:" + params.sender
                + ",receiver:" + params.receiver
                + ",command:" + params.command
                + ",asset-uuid:" + body.uuid;
    }
}
