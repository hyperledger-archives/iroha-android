package io.soramitsu.iroha.exception;

import android.content.Context;

import com.google.zxing.WriterException;

import io.soramitsu.iroha.R;
import io.soramitsu.irohaandroid.exception.AccountDuplicateException;
import io.soramitsu.irohaandroid.exception.HttpBadRequestException;
import io.soramitsu.irohaandroid.exception.UserNotFoundException;

public class ErrorMessageFactory {

    private ErrorMessageFactory() {
    }

    public static String create(Context context, Throwable exception, String... params) {
        String message;

        if (exception instanceof UserNotFoundException) {
            message = context.getString(R.string.error_message_user_not_found);
        } else if (exception instanceof AccountDuplicateException) {
            message = context.getString(R.string.error_message_account_duplicate);
        } else if (exception instanceof LargeNumberOfDigitsException) {
            message = context.getString(R.string.error_message_large_number_of_digits);
        } else if (exception instanceof IlligalQRCodeException) {
            message = context.getString(R.string.error_message_illegal_qr);
        } else if (exception instanceof IlligalRequestAmountException) {
            message = context.getString(R.string.error_message_request_amount_is_incorrect);
        } else if (exception instanceof SelfSendCanNotException) {
            message = context.getString(R.string.error_message_cannot_send_to_myself);
        } else if (exception instanceof RequiredArgumentException) {
            message = context.getString(R.string.validation_message_required, params);
        } else if (exception instanceof WriterException) {
            message = context.getString(R.string.error_message_cannot_generate_qr);
        } else if (exception instanceof NetworkNotConnectedException) {
            message = context.getString(R.string.error_message_check_network_state);
        } else if (exception instanceof HttpBadRequestException) {
            message = context.getString(R.string.error_message_retry_again);
        } else {
            message = context.getString(R.string.error_message_retry_again);
        }

        return message;
    }
}
