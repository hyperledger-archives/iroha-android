package io.soramitsu.irohaandroid.data.repository;

import java.util.List;

import io.soramitsu.irohaandroid.data.entity.DomainEntity;
import io.soramitsu.irohaandroid.data.entity.DomainListEntity;
import io.soramitsu.irohaandroid.data.entity.mapper.DomainEntityDataMapper;
import io.soramitsu.irohaandroid.data.repository.datasource.domain.DomainDataFactory;
import io.soramitsu.irohaandroid.data.repository.datasource.domain.DomainDataStore;
import io.soramitsu.irohaandroid.domain.entity.Domain;
import io.soramitsu.irohaandroid.domain.entity.reqest.DomainRegisterRequest;
import io.soramitsu.irohaandroid.domain.repository.DomainRepository;
import rx.Observable;
import rx.functions.Func1;

public class DomainDataRepository implements DomainRepository {

    private DomainDataFactory domainDataFactory;
    private DomainEntityDataMapper domainEntityDataMapper;

    public DomainDataRepository() {
        this.domainDataFactory = new DomainDataFactory();
        this.domainEntityDataMapper = new DomainEntityDataMapper();
    }

    @Override
    public Observable<Domain> register(DomainRegisterRequest body) {
        DomainDataStore domainDataStore = domainDataFactory.create();
        return domainDataStore.register(body).map(new Func1<DomainEntity, Domain>() {
            @Override
            public Domain call(DomainEntity domainEntity) {
                return domainEntityDataMapper.transform(domainEntity);
            }
        });
    }

    @Override
    public Observable<List<Domain>> domains() {
        DomainDataStore domainDataStore = domainDataFactory.create();
        return domainDataStore.domains().map(new Func1<DomainListEntity, List<Domain>>() {
            @Override
            public List<Domain> call(DomainListEntity domainListEntity) {
                return domainEntityDataMapper.transform(domainListEntity);
            }
        });
    }
}
