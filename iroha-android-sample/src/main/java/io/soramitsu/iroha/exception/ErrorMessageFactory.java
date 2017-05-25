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

import android.content.Context;

import com.google.zxing.WriterException;

import io.soramitsu.iroha.R;

public class ErrorMessageFactory {

    private ErrorMessageFactory() {
    }

    public static String create(Context context, Throwable exception, String... params) {
        String message;

        if (exception instanceof AccountNotFoundException) {
            message = context.getString(R.string.error_message_user_not_found);
        } else if (exception instanceof AccountDuplicateException) {
            message = context.getString(R.string.error_message_account_duplicate);
        } else if (exception instanceof LargeNumberOfDigitsException) {
            message = context.getString(R.string.error_message_large_number_of_digits);
        } else if (exception instanceof IllegalQRCodeException) {
            message = context.getString(R.string.error_message_illegal_qr);
        } else if (exception instanceof IllegalRequestAmountException) {
            message = context.getString(R.string.error_message_request_amount_is_incorrect);
        } else if (exception instanceof SelfSendCanNotException) {
            message = context.getString(R.string.error_message_cannot_send_to_myself);
        } else if (exception instanceof RequiredArgumentException) {
            message = context.getString(R.string.validation_message_required, (Object[]) params);
        } else if (exception instanceof WriterException) {
            message = context.getString(R.string.error_message_cannot_generate_qr);
        } else if (exception instanceof ReceiverNotFoundException) {
            message = context.getString(R.string.error_receiver_not_found);
        } else if (exception instanceof NetworkNotConnectedException) {
            message = context.getString(R.string.error_message_check_network_state);
        } else {
            message = context.getString(R.string.error_message_retry_again);
        }

        return message;
    }
}
