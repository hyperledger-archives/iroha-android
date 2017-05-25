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

package io.soramitsu.iroha.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Transaction implements Serializable {
    @SerializedName(value = "asset-uuid")
    public String assetUuid;
    @SerializedName(value = "alias")
    public String assetName;
    public OperationParameter params;
    public String signature;

    public String modifyDisplayDate() {
        long now = Calendar.getInstance().getTimeInMillis() / 1000;
        long sec = now - this.params.timestamp;
        if (sec <= 0) {
            return "now";
        } else if (sec < 60) {
            return sec + "sec";
        } else if (sec < 3600) {
            return Math.round(sec / 60) + "min";
        } else if (sec < 3600 * 24) {
            return Math.round(sec / (60 * 60)) + "hour";
        } else if (sec < 3600 * 24 * 31) {
            if (Math.round(sec / (60 * 60 * 24)) <= 1) {
                return Math.round(sec / (60 * 60 * 24)) + "day";
            } else {
                return Math.round(sec / (60 * 60 * 24)) + "days";
            }
        } else {
            Date date = new Date(this.params.timestamp * 1000);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
            return sdf.format(date);
        }
    }

    public boolean isSender(String publicKey) {
        return params.sender.equals(publicKey);
    }
}
