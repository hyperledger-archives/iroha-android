package io.soramitsu.iroha.models.apis;

import com.google.gson.Gson;

import java.util.List;

import io.soramitsu.iroha.models.Domain;


/**
 * Wrapper class of the Iroha domain transaction API.
 */
public class DomainClient extends TransactionClient<Domain> {
    private static DomainClient domainClient;

    private Gson gson;

    private DomainClient(Gson gson) {
        this.gson = gson;
    }

    /**
     * To generate new instance. (This is singleton)
     *
     * @return Instance of IrohaUserClient
     */
    public static DomainClient newInstance() {
        if (domainClient == null) {
            domainClient = new DomainClient(new Gson());
        }
        return domainClient;
    }

    @Override
    public Domain register() {
        return null;
    }

    @Override
    public List<Domain> list() {
        return null;
    }
}
