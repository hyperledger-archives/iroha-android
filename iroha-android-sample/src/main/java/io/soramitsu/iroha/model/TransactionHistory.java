package io.soramitsu.iroha.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import io.soramitsu.irohaandroid.model.Transaction;

public class TransactionHistory implements Parcelable {
    public static final String TAG = TransactionHistory.class.getSimpleName();

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

//    public static TransactionHistory createMock() {
//        TransactionHistory mock = new TransactionHistory();
//        mock.amount = "1000";
//        mock.histories = Transaction.createMock();
//        return mock;
//    }
}
