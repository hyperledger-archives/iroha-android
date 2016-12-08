package io.soramitsu.irohaandroid.service;

import java.io.IOException;
import java.util.List;

import io.soramitsu.irohaandroid.entity.mapper.DomainEntityDataMapper;
import io.soramitsu.irohaandroid.exception.HttpBadRequestException;
import io.soramitsu.irohaandroid.model.Domain;
import io.soramitsu.irohaandroid.net.dataset.reqest.DomainRegisterRequest;
import io.soramitsu.irohaandroid.repository.DomainRepository;
import io.soramitsu.irohaandroid.repository.impl.DomainRepositoryImpl;

public class DomainService {

    private final DomainRepository domainRepository = new DomainRepositoryImpl();
    private final DomainEntityDataMapper domainEntityDataMapper = new DomainEntityDataMapper();

    public Domain register(String name, String owner, String signature)
            throws IOException, HttpBadRequestException {

        final DomainRegisterRequest body = new DomainRegisterRequest();
        body.name = name;
        body.owner = owner;
        body.timestamp = System.currentTimeMillis() / 1000;
        body.signature = signature;

        return domainEntityDataMapper.transform(domainRepository.register(body));
    }

    public List<Domain> findDomains()
            throws IOException, HttpBadRequestException {
        return domainEntityDataMapper.transform(domainRepository.findDomains());
    }
}
