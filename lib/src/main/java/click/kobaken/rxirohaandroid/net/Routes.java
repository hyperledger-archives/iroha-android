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

package click.kobaken.rxirohaandroid.net;

public class Routes {
    private Routes() {
    }

    public static final String ACCOUNT_REGISTER = "/account/register";
    public static final String ACCOUNT_INFO = "/account";

    public static final String DOMAIN_REGISTER = "/domain/register";
    public static final String DOMAIN_LIST = "/domain/list";

    public static final String ASSET_CREATE = "/asset/create";
    public static final String ASSET_LIST = "/domain/list";
    public static final String ASSET_OPERATION = "/asset/operation";

    public static final String TRANSACTION_HISTORY_WITH_UUID = "/history/transaction";
    public static String TRANSACTION_HISTORY(final String domain, final String asset) {
        return "/history/" + domain + "/" + asset + "/transaction";
    }
}
