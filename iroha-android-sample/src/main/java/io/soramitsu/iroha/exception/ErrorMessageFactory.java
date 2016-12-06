package io.soramitsu.iroha.exception;

import android.content.Context;

import io.soramitsu.irohaandroid.data.exception.UserNotFoundException;

public class ErrorMessageFactory {

    private ErrorMessageFactory() {
    }

    public static String create(Context context, Exception exception) {
        String message = "";

        if (exception instanceof UserNotFoundException) {
            message = "";
        }

        return message;
    }
}
