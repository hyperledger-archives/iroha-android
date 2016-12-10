package io.soramitsu.iroha.exception;

import android.content.Context;

import io.soramitsu.irohaandroid.exception.UserNotFoundException;

public class ErrorMessageFactory {

    private ErrorMessageFactory() {
    }

    public static String create(Context context, Exception exception) {
        String message = "";

        if (exception instanceof UserNotFoundException) {
            message = "";
        } else if (exception instanceof LargeNumberOfDigitsException) {
            message = "桁数が多すぎます";
        }

        return message;
    }
}
