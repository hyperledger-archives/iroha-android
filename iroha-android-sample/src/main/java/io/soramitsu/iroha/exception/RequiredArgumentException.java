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

package io.soramitsu.iroha.exception;

public class RequiredArgumentException extends RuntimeException {
    public RequiredArgumentException() {
    }

    public RequiredArgumentException(String message) {
        super(message);
    }

    public RequiredArgumentException(String message, Throwable cause) {
        super(message, cause);
    }

    public RequiredArgumentException(Throwable cause) {
        super(cause);
    }
}
