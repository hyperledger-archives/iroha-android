package io.soramitsu.iroha.util;

public class DateUtil {
    private DateUtil() {
    }

    public static long currentUnixTimestamp() {
        return System.currentTimeMillis() / 1000;
    }
}
