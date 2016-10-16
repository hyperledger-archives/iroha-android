package io.soramitsu.irohaandroid.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;


/**
 * Iroha domain and asset history.
 */
public class History extends ResponseObject {
    private String uuid;
    private long timestamp;
    private List<HistoryValue> history;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public List<HistoryValue> getHistory() {
        return history;
    }

    public void setHistory(List<HistoryValue> history) {
        this.history = history;
    }

    class HistoryValue {
        @SerializedName("asset-uuid")
        private String assetUuid;
        private Params params;

        public String getAssetUuid() {
            return assetUuid;
        }

        public void setAssetUuid(String assetUuid) {
            this.assetUuid = assetUuid;
        }

        public Params getParams() {
            return params;
        }

        public void setParams(Params params) {
            this.params = params;
        }

        class Params {
            private String sender;
            private String amount;

            public String getSender() {
                return sender;
            }

            public void setSender(String sender) {
                this.sender = sender;
            }

            public String getAmount() {
                return amount;
            }

            public void setAmount(String amount) {
                this.amount = amount;
            }
        }
    }
}
