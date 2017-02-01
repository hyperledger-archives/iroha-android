/*
Copyright(c) 2016 kobaken0029 All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package click.kobaken.rxirohaandroid.service;

import java.util.List;

import click.kobaken.rxirohaandroid.model.Domain;
import click.kobaken.rxirohaandroid.net.dataset.reqest.DomainRegisterRequest;
import click.kobaken.rxirohaandroid.repository.DomainRepository;
import io.reactivex.Observable;


public class DomainService {
    private DomainRepository domainRepository;

    public DomainService(DomainRepository domainRepository) {
        this.domainRepository = domainRepository;
    }

    public Observable<Domain> register(String name, String owner, String signature) {

        final DomainRegisterRequest body = new DomainRegisterRequest();
        body.name = name;
        body.owner = owner;
        body.timestamp = System.currentTimeMillis() / 1000;
        body.signature = signature;

        return domainRepository.register(body);
    }

    public Observable<List<Domain>> findDomains(final int limit, final int offset) {
        return domainRepository.findDomains(limit, offset);
    }
}
