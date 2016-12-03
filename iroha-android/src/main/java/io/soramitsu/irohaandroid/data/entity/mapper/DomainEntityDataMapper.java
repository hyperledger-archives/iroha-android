package io.soramitsu.irohaandroid.data.entity.mapper;

import java.util.List;

import io.soramitsu.irohaandroid.data.entity.DomainEntity;
import io.soramitsu.irohaandroid.data.entity.DomainListEntity;
import io.soramitsu.irohaandroid.domain.entity.Domain;
import rx.Observable;
import rx.functions.Func1;

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
        Func1<DomainEntity, Domain> convertAction = new Func1<DomainEntity, Domain>() {
            @Override
            public Domain call(DomainEntity domainEntity) {
                return transform(domainEntity);
            }
        };
        return Observable.from(domainListEntity.list)
                .map(convertAction)
                .toList()
                .toBlocking()
                .single();
    }
}
