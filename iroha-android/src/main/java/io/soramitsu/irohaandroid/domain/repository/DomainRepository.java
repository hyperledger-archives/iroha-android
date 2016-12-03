package io.soramitsu.irohaandroid.domain.repository;

import java.util.List;

import io.soramitsu.irohaandroid.domain.entity.Domain;
import io.soramitsu.irohaandroid.domain.entity.reqest.DomainRegisterRequest;
import rx.Observable;

public interface DomainRepository {
    Observable<Domain> register(DomainRegisterRequest body);

    Observable<List<Domain>> domains();
}
