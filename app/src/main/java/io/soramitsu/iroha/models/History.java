package io.soramitsu.iroha.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;


/**
 * Iroha domain and asset history.
 */
public class History extends ResponseObject {
    private String uuid;
    private long timestamp;
    private List<HistoryValue> history;

    class HistoryValue {
        @SerializedName("asset-uuid")
        private String assetUuid;
        private Params params;

        class Params {
            private String sender;
            private String amount;
        }
    }
}
