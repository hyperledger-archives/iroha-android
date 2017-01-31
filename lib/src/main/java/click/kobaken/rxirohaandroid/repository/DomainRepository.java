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

package click.kobaken.rxirohaandroid.repository;

import java.io.IOException;

import click.kobaken.rxirohaandroid.entity.DomainEntity;
import click.kobaken.rxirohaandroid.entity.DomainListEntity;
import click.kobaken.rxirohaandroid.exception.HttpBadRequestException;
import click.kobaken.rxirohaandroid.net.dataset.reqest.DomainRegisterRequest;


public interface DomainRepository {
    DomainEntity register(DomainRegisterRequest body)
            throws IOException, HttpBadRequestException;

    DomainListEntity findDomains(int limit, int offset)
            throws IOException, HttpBadRequestException;
}
