package io.soramitsu.iroha.models.apis;

import java.util.List;

import io.soramitsu.iroha.models.Asset;
import io.soramitsu.iroha.models.Domain;
import io.soramitsu.iroha.models.History;
import io.soramitsu.iroha.models.ResponseObject;


/**
 * Wrapper class of the Iroha transaction API.
 */
public abstract class TransactionClient<T extends ResponseObject> {
    public abstract T register();
    public abstract List<T> list();

    public History history(String uuid) {
        return null;
    }

    public History history(Domain domain, Asset asset) {
        return null;
    }

    public ResponseObject message() {
        return null;
    }
}
