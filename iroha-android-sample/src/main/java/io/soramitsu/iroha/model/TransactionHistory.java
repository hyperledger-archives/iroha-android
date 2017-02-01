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

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import click.kobaken.rxirohaandroid.model.Transaction;


public class TransactionHistory implements Parcelable {
    public static final String TAG = TransactionHistory.class.getSimpleName();

    public static final String TRANSFER = "Transfer";

    public String value;
    public List<Transaction> histories;

    public TransactionHistory() {
    }

    protected TransactionHistory(Parcel in) {
        this.value = in.readString();
        this.histories = new ArrayList<>();
        in.readList(this.histories, Transaction.class.getClassLoader());
    }

    public static final Creator<TransactionHistory> CREATOR = new Creator<TransactionHistory>() {
        @Override
        public TransactionHistory createFromParcel(Parcel source) {
            return new TransactionHistory(source);
        }

        @Override
        public TransactionHistory[] newArray(int size) {
            return new TransactionHistory[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.value);
        dest.writeList(this.histories);
    }
}
