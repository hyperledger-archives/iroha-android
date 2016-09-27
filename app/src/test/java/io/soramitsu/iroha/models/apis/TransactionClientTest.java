package io.soramitsu.iroha.models.apis;

import junit.framework.TestCase;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.security.KeyPair;
import java.util.List;

import io.soramitsu.iroha.models.Asset;
import io.soramitsu.iroha.models.Domain;
import io.soramitsu.iroha.models.History;
import io.soramitsu.iroha.models.ResponseObject;
import io.soramitsu.iroha.utils.NetworkMockUtil;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;

import static io.soramitsu.iroha.utils.DigestUtil.createKeyPair;
import static io.soramitsu.iroha.utils.NetworkMockUtil.call;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


@RunWith(JUnit4.class)
public class TransactionClientTest extends TestCase {
    private static final int TIMEOUT = 10000;

    private static final String TEST_UUID = "1234";
    private static final String TEST_DOMAIN_NAME = "domain_name";
    private static final String TEST_ASSET_NAME = "asset_name";

    private TransactionClient transactionClient = TransactionClient.newInstance();

    @BeforeClass
    public static void setUpOnce() throws Exception {
        final Dispatcher dispatcher = new Dispatcher() {

            @Override
            public MockResponse dispatch(final RecordedRequest request)
                    throws InterruptedException {
                if (request == null || request.getPath() == null) {
                    return new MockResponse().setResponseCode(400);
                }
                switch (request.getPath()) {
                    case "//domain/register":
                        if (request.getMethod().equals("POST")) {
                            return new MockResponse().setBody("{\n" +
                                    "  \"status\": 200,\n" +
                                    "  \"message\": \"Domain registered successfully.\"\n" +
                                    "}").setResponseCode(200);
                        }
                        return new MockResponse().setResponseCode(400);
                    case "//asset/create":
                        if (request.getMethod().equals("POST")) {
                            return new MockResponse().setBody("{\n" +
                                    "  \"status\": 200,\n" +
                                    "  \"message\": \"Asset created successfully.\"\n" +
                                    "}").setResponseCode(200);
                        }
                        return new MockResponse().setResponseCode(400);
                    case "//domain/list":
                        if (request.getMethod().equals("GET")) {
                            return new MockResponse().setBody("[\n" +
                                    "    {\n" +
                                    "      \"domain\" : \"soramitsu\",\n" +
                                    "      \"creator\" : \"base64_public_key\",\n" +
                                    "      \"signature\" : \"b64encoded?_signature\",\n" +
                                    "      \"creationDate\" : \"time\"\n" +
                                    "    },\n" +
                                    "    {\n" +
                                    "      \"domain\" : \"iroha\",\n" +
                                    "      \"creator\" : \"base64_public_key\",\n" +
                                    "      \"signature\" : \"b64encoded?_signature\",\n" +
                                    "      \"creationDate\" : \"time\"\n" +
                                    "    }\n" +
                                    "  ]").setResponseCode(200);
                        }
                        return new MockResponse().setResponseCode(400);
                    case "/1/domain/list":
                        if (request.getMethod().equals("GET")) {
                            return new MockResponse().setBody("[\n" +
                                    "    {\n" +
                                    "      \"domain\" : \"soramitsu\",\n" +
                                    "      \"creator\" : \"base64_public_key\",\n" +
                                    "      \"signature\" : \"b64encoded?_signature\",\n" +
                                    "      \"creationDate\" : \"time\"\n" +
                                    "    }\n" +
                                    "  ]").setResponseCode(200);
                        }
                        return new MockResponse().setResponseCode(400);
                    case "/0/domain/list":
                        if (request.getMethod().equals("GET")) {
                            return new MockResponse().setBody("[]").setResponseCode(200);
                        }
                        return new MockResponse().setResponseCode(400);
                    case "//asset/list/" + TEST_DOMAIN_NAME:
                        if (request.getMethod().equals("GET")) {
                            return new MockResponse().setBody("[\n" +
                                    "    {\n" +
                                    "      \"name\" : \"moeka\",\n" +
                                    "      \"creator\" : \"base64_public_key\",\n" +
                                    "      \"signature\" : \"b64encoded?_signature\",\n" +
                                    "      \"creationDate\" : \"time\"\n" +
                                    "    },\n" +
                                    "    {\n" +
                                    "      \"name\" : \"iroha\",\n" +
                                    "      \"creator\" : \"base64_public_key\",\n" +
                                    "      \"signature\" : \"b64encoded?_signature\",\n" +
                                    "      \"creationDate\" : \"time\"\n" +
                                    "    }\n" +
                                    "  ]\n").setResponseCode(200);
                        }
                        return new MockResponse().setResponseCode(400);
                    case "/1/asset/list/" + TEST_DOMAIN_NAME:
                        if (request.getMethod().equals("GET")) {
                            return new MockResponse().setBody("[\n" +
                                    "    {\n" +
                                    "      \"name\" : \"moeka\",\n" +
                                    "      \"creator\" : \"base64_public_key\",\n" +
                                    "      \"signature\" : \"b64encoded?_signature\",\n" +
                                    "      \"creationDate\" : \"time\"\n" +
                                    "    }\n" +
                                    "  ]\n").setResponseCode(200);
                        }
                        return new MockResponse().setResponseCode(400);
                    case "/0/asset/list/" + TEST_DOMAIN_NAME:
                        if (request.getMethod().equals("GET")) {
                            return new MockResponse().setBody("[]").setResponseCode(200);
                        }
                        return new MockResponse().setResponseCode(400);
                    case "//asset/operation":
                        if (request.getMethod().equals("POST")) {
                            return new MockResponse().setBody("{\n" +
                                    "  \"status\": 200,\n" +
                                    "  \"message\": \"Asset transfered successfully.\"\n" +
                                    "}").setResponseCode(200);
                        }
                        return new MockResponse().setResponseCode(400);
                    case "//history/transaction/" + TEST_UUID:
                        if (request.getMethod().equals("GET")) {
                            return new MockResponse().setBody("{\n" +
                                    "  \"uuid\" : \"user's_uuid\",\n" +
                                    "  \"timestamp\" : 1234567,\n" +
                                    "  \"history\" : [\n" +
                                    "    {\n" +
                                    "      \"asset-uuid\":\"sha3\",\n" +
                                    "      \"params\":{\n" +
                                    "        \"sender\" : \"pubkey\",\n" +
                                    "        \"amount\" : \"753\"\n" +
                                    "      }\n" +
                                    "    },\n" +
                                    "    {\n" +
                                    "      \"asset-uuid\":\"sha3\",\n" +
                                    "      \"params\":{\n" +
                                    "        \"sender\" : \"pubkey\",\n" +
                                    "        \"amount\" : \"753\"\n" +
                                    "      }\n" +
                                    "    }\n" +
                                    "  ]\n" +
                                    "}").setResponseCode(200);
                        }
                        return new MockResponse().setResponseCode(400);
                    case "/1/history/transaction/" + TEST_UUID:
                        if (request.getMethod().equals("GET")) {
                            return new MockResponse().setBody("{\n" +
                                    "  \"uuid\" : \"user's_uuid\",\n" +
                                    "  \"timestamp\" : 1234567,\n" +
                                    "  \"history\" : [\n" +
                                    "    {\n" +
                                    "      \"asset-uuid\":\"sha3\",\n" +
                                    "      \"params\":{\n" +
                                    "        \"sender\" : \"pubkey\",\n" +
                                    "        \"amount\" : \"753\"\n" +
                                    "      }\n" +
                                    "    }\n" +
                                    "  ]\n" +
                                    "}").setResponseCode(200);
                        }
                        return new MockResponse().setResponseCode(400);
                    case "/0/history/transaction/" + TEST_UUID:
                        if (request.getMethod().equals("GET")) {
                            return new MockResponse().setBody("{\n" +
                                    "  \"uuid\" : \"user's_uuid\",\n" +
                                    "  \"timestamp\" : 1234567,\n" +
                                    "  \"history\" : []\n" +
                                    "}").setResponseCode(200);
                        }
                        return new MockResponse().setResponseCode(400);
                    case "//history/" + TEST_DOMAIN_NAME + "." + TEST_ASSET_NAME:
                        if (request.getMethod().equals("GET")) {
                            return new MockResponse().setBody("{\n" +
                                    "  \"uuid\" : \"user's_uuid\",\n" +
                                    "  \"timestamp\" : 1234567,\n" +
                                    "  \"history\" : [\n" +
                                    "    {\n" +
                                    "      \"asset-uuid\":\"sha3\",\n" +
                                    "      \"params\":{\n" +
                                    "        \"sender\" : \"pubkey\",\n" +
                                    "        \"amount\" : \"753\"\n" +
                                    "      }\n" +
                                    "    },\n" +
                                    "    {\n" +
                                    "      \"asset-uuid\":\"sha3\",\n" +
                                    "      \"params\":{\n" +
                                    "        \"sender\" : \"pubkey\",\n" +
                                    "        \"amount\" : \"753\"\n" +
                                    "      }\n" +
                                    "    }\n" +
                                    "  ]\n" +
                                    "}").setResponseCode(200);
                        }
                        return new MockResponse().setResponseCode(400);
                    case "/1/history/" + TEST_DOMAIN_NAME + "." + TEST_ASSET_NAME:
                        if (request.getMethod().equals("GET")) {
                            return new MockResponse().setBody("{\n" +
                                    "  \"uuid\" : \"user's_uuid\",\n" +
                                    "  \"timestamp\" : 1234567,\n" +
                                    "  \"history\" : [\n" +
                                    "    {\n" +
                                    "      \"asset-uuid\":\"sha3\",\n" +
                                    "      \"params\":{\n" +
                                    "        \"sender\" : \"pubkey\",\n" +
                                    "        \"amount\" : \"753\"\n" +
                                    "      }\n" +
                                    "    }\n" +
                                    "  ]\n" +
                                    "}").setResponseCode(200);
                        }
                        return new MockResponse().setResponseCode(400);
                    case "/0/history/" + TEST_DOMAIN_NAME + "." + TEST_ASSET_NAME:
                        if (request.getMethod().equals("GET")) {
                            return new MockResponse().setBody("{\n" +
                                    "  \"uuid\" : \"user's_uuid\",\n" +
                                    "  \"timestamp\" : 1234567,\n" +
                                    "  \"history\" : []\n" +
                                    "}").setResponseCode(200);
                        }
                        return new MockResponse().setResponseCode(400);
                    case "//message":
                        if (request.getMethod().equals("POST")) {
                            return new MockResponse().setBody("{\n" +
                                    "  \"status\": 200,\n" +
                                    "  \"message\": \"Message sent successfully.\"\n" +
                                    "}").setResponseCode(200);
                        }
                        return new MockResponse().setResponseCode(400);
                    default:
                        return new MockResponse().setResponseCode(404);
                }
            }
        };
        NetworkMockUtil.createMockWebServer(dispatcher);
    }

    @AfterClass
    public static void tearDownOnce() throws Exception {
        NetworkMockUtil.shutdownMockWebServer();
    }

    @Test(timeout = TIMEOUT)
    public void testRegisterDomain_Successful() throws Exception {
        final String name = "domain_name";
        final KeyPair keyPair = createKeyPair();

        ResponseObject result = transactionClient.registerDomain(call(""), name, keyPair);

        assertThat(result.getStatus(), is(200));
        assertThat(result.getMessage(), is("Domain registered successfully."));
    }

    @Test(timeout = TIMEOUT)
    public void testRegisterDomain_NotFound() throws Exception {
        final String name = "domain_name";
        final KeyPair keyPair = createKeyPair();

        ResponseObject result = transactionClient.registerDomain(call("/nothing"), name, keyPair);

        assertThat(result.getStatus(), is(404));
    }

    @Test(timeout = TIMEOUT)
    public void testRegisterAsset_Successful() throws Exception {
        final String name = "asset_name";
        final String domainName = "domain_name";
        final KeyPair keyPair = createKeyPair();

        ResponseObject result = transactionClient.registerAsset(call(""), name, domainName, keyPair);

        assertThat(result.getStatus(), is(200));
        assertThat(result.getMessage(), is("Asset created successfully."));
    }

    @Test(timeout = TIMEOUT)
    public void testRegisterAsset_NotFound() throws Exception {
        final String name = "asset_name";
        final String domainName = "domain_name";
        final KeyPair keyPair = createKeyPair();

        ResponseObject result = transactionClient.registerAsset(call("/nothing"), name, domainName, keyPair);

        assertThat(result.getStatus(), is(404));
    }

    @Test(timeout = TIMEOUT)
    public void testFindDomains_Successful_OneDomain() throws Exception {
        List<Domain> domains = transactionClient.findDomains(call("1"));

        assertThat(domains.size(), is(1));
        assertThat(domains.get(0).getName(), is("soramitsu"));
    }

    @Test(timeout = TIMEOUT)
    public void testFindDomains_Successful_MultiDomains() throws Exception {
        List<Domain> domains = transactionClient.findDomains(call(""));

        assertThat(domains.size(), is(2));
        assertThat(domains.get(0).getName(), is("soramitsu"));
        assertThat(domains.get(1).getName(), is("iroha"));
    }

    @Test(timeout = TIMEOUT)
    public void testFindDomains_Successful_DomainsNotFound() throws Exception {
        List<Domain> domains = transactionClient.findDomains(call("0"));

        assertThat(domains.size(), is(0));
    }

    @Test(timeout = TIMEOUT)
    public void testFindDomains_NotFound() throws Exception {
        List<Domain> domains = transactionClient.findDomains(call("/nothing"));

        assertThat(domains.size(), is(0));
    }

    @Test(timeout = TIMEOUT)
    public void testFindAssets_Successful_OneDomain() throws Exception {
        final String domain = "domain_name";

        List<Asset> assets = transactionClient.findAssets(call("1"), domain);

        assertThat(assets.size(), is(1));
        assertThat(assets.get(0).getName(), is("moeka"));
    }

    @Test(timeout = TIMEOUT)
    public void testFindAssets_Successful_MultiDomains() throws Exception {
        final String domain = "domain_name";

        List<Asset> assets = transactionClient.findAssets(call("/"), domain);

        assertThat(assets.size(), is(2));
        assertThat(assets.get(0).getName(), is("moeka"));
        assertThat(assets.get(1).getName(), is("iroha"));
    }

    @Test(timeout = TIMEOUT)
    public void testFindAssets_Successful_DomainsNotFound() throws Exception {
        final String domain = "domain_name";

        List<Asset> assets = transactionClient.findAssets(call("0"), domain);

        assertThat(assets.size(), is(0));
    }

    @Test(timeout = TIMEOUT)
    public void testFindAssets_NotFound() throws Exception {
        final String domain = "domain_name";

        List<Asset> assets = transactionClient.findAssets(call("/nothing"), domain);

        assertThat(assets.size(), is(0));
    }

    @Test(timeout = TIMEOUT)
    public void testOperation_Successful() throws Exception {
        final String assetUuid = "";
        final String command = "command";
        final int amount = 1000;
        final String receiver = "receiver";
        final KeyPair keyPair = createKeyPair();

        ResponseObject result = transactionClient.operation(call(""), assetUuid, command,
                amount, receiver, keyPair);

        assertThat(result.getStatus(), is(200));
        assertThat(result.getMessage(), is("Asset transfered successfully."));
    }

    @Test(timeout = TIMEOUT)
    public void testOperation_NotFound() throws Exception {
        final String assetUuid = "";
        final String command = "command";
        final int amount = 1000;
        final String receiver = "receiver";
        final KeyPair keyPair = createKeyPair();

        ResponseObject result = transactionClient.operation(call("/nothing"), assetUuid, command,
                amount, receiver, keyPair);

        assertThat(result.getStatus(), is(404));
    }

    @Test(timeout = TIMEOUT)
    public void testHistory_Successful_OneHistory() throws Exception {
        final String uuid = "1234";

        History result = transactionClient.history(call("1"), uuid);

        assertThat(result.getStatus(), is(200));
        assertThat(result.getHistory().size(), is(1));
    }

    @Test(timeout = TIMEOUT)
    public void testHistory_Successful_MultiHistory() throws Exception {
        final String uuid = "1234";

        History result = transactionClient.history(call(""), uuid);

        assertThat(result.getStatus(), is(200));
        assertThat(result.getHistory().size(), is(2));
    }

    @Test(timeout = TIMEOUT)
    public void testHistory_Successful_HistoryNotFound() throws Exception {
        final String uuid = "1234";

        History result = transactionClient.history(call("0"), uuid);

        assertThat(result.getStatus(), is(200));
        assertThat(result.getHistory().size(), is(0));
    }

    @Test(timeout = TIMEOUT)
    public void testHistory_NotFound() throws Exception {
        final String uuid = "1234";

        History result = transactionClient.history(call("/nothing"), uuid);

        assertThat(result.getStatus(), is(404));
    }

    @Test(timeout = TIMEOUT)
    public void testHistory_WithDomainAndAsset_Successful_OneHistory() throws Exception {
        final String domain = "domain_name";
        final String asset = "asset_name";

        History result = transactionClient.history(call("1"), domain, asset);

        assertThat(result.getStatus(), is(200));
        assertThat(result.getHistory().size(), is(1));
    }

    @Test(timeout = TIMEOUT)
    public void testHistory_WithDomainAndAsset_Successful_MultiHistory() throws Exception {
        final String domain = "domain_name";
        final String asset = "asset_name";

        History result = transactionClient.history(call(""), domain, asset);

        assertThat(result.getStatus(), is(200));
        assertThat(result.getHistory().size(), is(2));
    }

    @Test(timeout = TIMEOUT)
    public void testHistory_WithDomainAndAsset_Successful_HistoryNotFound() throws Exception {
        final String domain = "domain_name";
        final String asset = "asset_name";

        History result = transactionClient.history(call("0"), domain, asset);

        assertThat(result.getStatus(), is(200));
        assertThat(result.getHistory().size(), is(0));
    }

    @Test(timeout = TIMEOUT)
    public void testHistory_WithDomainAndAsset_NotFound() throws Exception {
        final String domain = "domain_name";
        final String asset = "asset_name";

        History result = transactionClient.history(call("/nothing"), domain, asset);

        assertThat(result.getStatus(), is(404));
    }

    @Test(timeout = TIMEOUT)
    public void testSendMessage_Successful() throws Exception {
        final String message = "message";
        final String receiver = "receiver";
        final KeyPair keyPair = createKeyPair();

        ResponseObject result = transactionClient.sendMessage(call(""), message, receiver, keyPair);

        assertThat(result.getStatus(), is(200));
        assertThat(result.getMessage(), is("Message sent successfully."));
    }

    @Test(timeout = TIMEOUT)
    public void testSendMessage_NotFound() throws Exception {
        final String message = "message";
        final String receiver = "receiver";
        final KeyPair keyPair = createKeyPair();

        ResponseObject result = transactionClient.sendMessage(call("/nothing"), message, receiver, keyPair);

        assertThat(result.getStatus(), is(404));
    }
}
