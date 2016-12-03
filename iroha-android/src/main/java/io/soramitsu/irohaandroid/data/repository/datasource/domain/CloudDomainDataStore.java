package io.soramitsu.irohaandroid.data.repository.datasource.domain;

import io.soramitsu.irohaandroid.data.entity.DomainEntity;
import io.soramitsu.irohaandroid.data.entity.DomainListEntity;
import io.soramitsu.irohaandroid.data.net.RestApi;
import io.soramitsu.irohaandroid.domain.entity.reqest.DomainRegisterRequest;
import rx.Observable;

public class CloudDomainDataStore implements DomainDataStore {

    private RestApi restApi;

    CloudDomainDataStore(RestApi restApi) {
        this.restApi = restApi;
    }

    @Override
    public Observable<DomainEntity> register(DomainRegisterRequest body) {
        return restApi.domainService().register(body);
    }

    @Override
    public Observable<DomainListEntity> domains() {
        return restApi.domainService().domains();
    }
}
