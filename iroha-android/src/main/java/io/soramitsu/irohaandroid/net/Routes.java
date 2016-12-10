package io.soramitsu.irohaandroid.net;

public class Routes {
    private Routes() {
    }

    public static final String ACCOUNT_REGISTER = "/account/register";
    public static final String ACCOUNT_INFO = "/account?uuid=";

    public static final String DOMAIN_REGISTER = "/domain/register";
    public static final String DOMAIN_LIST = "/domain/list";

    public static final String ASSET_CREATE = "/asset/create";
    public static final String ASSET_LIST = "/domain/list";
    public static final String ASSET_OPERATION = "/asset/operation";

    public static final String TRANSACTION_HISTORY_WITH_UUID = "/history/transaction?uuid=";
    public static String TRANSACTION_HISTORY(final String domain, final String asset) {
        return "/history/" + domain + "/" + asset + "/transaction?uuid=";
    }
}
