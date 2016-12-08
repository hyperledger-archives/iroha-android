package io.soramitsu.irohaandroid.repository;

import java.io.IOException;

import io.soramitsu.irohaandroid.entity.DomainEntity;
import io.soramitsu.irohaandroid.entity.DomainListEntity;
import io.soramitsu.irohaandroid.exception.HttpBadRequestException;
import io.soramitsu.irohaandroid.net.dataset.reqest.DomainRegisterRequest;

public interface DomainRepository {
    DomainEntity register(DomainRegisterRequest body)
            throws IOException, HttpBadRequestException;

    DomainListEntity findDomains()
            throws IOException, HttpBadRequestException;
}
