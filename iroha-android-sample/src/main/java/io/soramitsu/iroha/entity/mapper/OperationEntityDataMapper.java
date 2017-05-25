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

package io.soramitsu.iroha.entity.mapper;

import io.soramitsu.iroha.entity.OperationParameterEntity;
import io.soramitsu.iroha.model.OperationParameter;

public class OperationEntityDataMapper {
    public static OperationParameter transform(OperationParameterEntity operationParameterEntity) {
        OperationParameter operationParameter = null;

        if (operationParameterEntity != null) {
            operationParameter = new OperationParameter();
            operationParameter.command = operationParameterEntity.command;
            operationParameter.value = operationParameterEntity.value;
            operationParameter.sender = operationParameterEntity.sender;
            operationParameter.receiver = operationParameterEntity.receiver;
            operationParameter.oppoent = operationParameterEntity.opponent;
            operationParameter.timestamp = operationParameterEntity.timestamp;
        }

        return operationParameter;
    }
}
