package io.soramitsu.irohaandroid.entity.mapper;

import java.util.ArrayList;
import java.util.List;

import io.soramitsu.irohaandroid.entity.DomainEntity;
import io.soramitsu.irohaandroid.entity.DomainListEntity;
import io.soramitsu.irohaandroid.model.Domain;

public class DomainEntityDataMapper {

    public Domain transform(DomainEntity domainEntity) {
        Domain domain = null;

        if (domainEntity != null) {
            domain = new Domain();
            domain.name = domainEntity.name;
            domain.owner = domainEntity.owner;
            domain.signature = domainEntity.signature;
            domain.timestamp = domainEntity.timestamp;
        }

        return domain;
    }

    public List<Domain> transform(DomainListEntity domainListEntity) {
        List<Domain> domains = new ArrayList<>();
        for (DomainEntity domainEntity : domainListEntity.list) {
            domains.add(transform(domainEntity));
        }
        return domains;
    }
}
