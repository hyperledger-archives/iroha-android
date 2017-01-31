/*
Copyright Soramitsu Co., Ltd. 2016 All Rights Reserved.
http://soramitsu.co.jp

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

import java.io.IOException;
import java.util.List;

import click.kobaken.rxirohaandroid.entity.mapper.DomainEntityDataMapper;
import click.kobaken.rxirohaandroid.exception.HttpBadRequestException;
import click.kobaken.rxirohaandroid.model.Domain;
import click.kobaken.rxirohaandroid.net.dataset.reqest.DomainRegisterRequest;
import click.kobaken.rxirohaandroid.repository.DomainRepository;
import click.kobaken.rxirohaandroid.repository.impl.DomainRepositoryImpl;


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

    public List<Domain> findDomains(final int limit, final int offset)
            throws IOException, HttpBadRequestException {
        return domainEntityDataMapper.transform(domainRepository.findDomains(limit, offset));
    }
}
