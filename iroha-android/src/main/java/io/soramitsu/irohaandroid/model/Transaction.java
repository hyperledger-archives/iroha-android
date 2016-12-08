package io.soramitsu.irohaandroid.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    public long timestamp;

    public static ArrayList<Transaction> createMock() {
        ArrayList<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction(){{
            this.params = new OperationParameter();
            this.params.value = "100";
            this.params.sender = "test1";
        }});
        transactions.add(new Transaction(){{
            this.params = new OperationParameter();
            this.params.value = "200";
            this.params.sender = "test2";
        }});
        transactions.add(new Transaction(){{
            this.params = new OperationParameter();
            this.params.value = "300";
            this.params.sender = "test3";
        }});
        transactions.add(new Transaction(){{
            this.params = new OperationParameter();
            this.params.value = "100";
            this.params.sender = "test1";
        }});
        transactions.add(new Transaction(){{
            this.params = new OperationParameter();
            this.params.value = "200";
            this.params.sender = "test2";
        }});
        transactions.add(new Transaction(){{
            this.params = new OperationParameter();
            this.params.value = "300";
            this.params.sender = "test3";
        }});
        transactions.add(new Transaction(){{
            this.params = new OperationParameter();
            this.params.value = "100";
            this.params.sender = "test1";
        }});
        transactions.add(new Transaction(){{
            this.params = new OperationParameter();
            this.params.value = "200";
            this.params.sender = "test2";
        }});
        transactions.add(new Transaction(){{
            this.params = new OperationParameter();
            this.params.value = "300";
            this.params.sender = "test3";
        }});
        return transactions;
    }

    public String modifyDisplayDate() {
        long now = Calendar.getInstance().getTimeInMillis() / 1000;
        long sec = now - timestamp;
        if (sec <= 0) {
            return "now";
        } else if (sec < 60) {
            return sec + "秒";
        } else if (sec < 3600) {
            return Math.round(sec / 60) + "分";
        } else if (sec < 3600 * 24) {
            return Math.round(sec / (60 * 60)) + "時間";
        } else if (sec < 3600 * 24 * 31) {
            return Math.round(sec / (60 * 60 * 24)) + "日";
        } else {
            Date date = new Date(timestamp * 1000);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
            return sdf.format(date);
        }
    }

    public boolean isSender(String publicKey) {
        return params.sender.equals(publicKey);
    }

    public static class OperationParameter implements Serializable {
        public String command;
        public String value;
        public String sender;
        public String receiver;
    }
}
