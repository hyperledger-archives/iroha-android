package io.soramitsu.irohaandroid.data.repository.datasource.domain;

import io.soramitsu.irohaandroid.data.entity.DomainEntity;
import io.soramitsu.irohaandroid.data.entity.DomainListEntity;
import io.soramitsu.irohaandroid.domain.entity.reqest.DomainRegisterRequest;
import rx.Observable;

public interface DomainDataStore {
    Observable<DomainEntity> register(DomainRegisterRequest body);
    Observable<DomainListEntity> domains();
}
