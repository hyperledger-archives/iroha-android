package io.soramitsu.iroha.models.apis;

import junit.framework.TestCase;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import io.soramitsu.iroha.utils.NetworkMockUtil;
import io.soramitsu.iroha.utils.NetworkUtil;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;


@RunWith(JUnit4.class)
public class TransactionClientTest extends TestCase {
    private static final int TIMEOUT = 10000;

    private static final String TEST_UUID = "1234";
    private static final String TEST_DOMAIN_NAME = "domain";
    private static final String TEST_ASSET_NAME = "asset";

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
                        return new MockResponse().setResponseCode(400);
                    case "//asset/create":
                        return new MockResponse().setResponseCode(400);
                    case "//domain/list":
                        return new MockResponse().setResponseCode(400);
                    case "//asset/list":
                        return new MockResponse().setResponseCode(400);
                    case "//asset/operation":
                        return new MockResponse().setResponseCode(400);
                    case "//history/transaction" + TEST_UUID:
                        return new MockResponse().setResponseCode(400);
                    case "//history/" + TEST_DOMAIN_NAME + "." + TEST_ASSET_NAME:
                        return new MockResponse().setResponseCode(400);
                    case "//message":
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

    @Before
    public void setUp() throws Exception {
        NetworkUtil.ENDPOINT_URL = NetworkMockUtil.call("").toString();
    }

    @Test
    public void testRegisterDomain_Successful() throws Exception {
        fail();
    }

    @Test
    public void testRegisterDomain_Duplicate() throws Exception {
        fail();
    }

    @Test
    public void testRegisterDomain_NotFound() throws Exception {
        fail();
    }

    @Test
    public void testRegisterAsset_Successful() throws Exception {
        fail();
    }

    @Test
    public void testRegisterAsset_Duplicate() throws Exception {
        fail();
    }

    @Test
    public void testRegisterAsset_NotFound() throws Exception {
        fail();
    }

    @Test
    public void testFindDomains_Successful_OneDomain() throws Exception {
        fail();
    }

    @Test
    public void testFindDomains_Successful_MultiDomains() throws Exception {
        fail();
    }

    @Test
    public void testFindDomains_Successful_DomainsNotFound() throws Exception {
        fail();
    }

    @Test
    public void testFindDomains_NotFound() throws Exception {
        fail();
    }

    @Test
    public void testFindAssets_Successful_OneDomain() throws Exception {
        fail();
    }

    @Test
    public void testFindAssets_Successful_MultiDomains() throws Exception {
        fail();
    }

    @Test
    public void testFindAssets_Successful_DomainsNotFound() throws Exception {
        fail();
    }

    @Test
    public void testFindAssets_NotFound() throws Exception {
        fail();
    }

    @Test
    public void testOperation_Successful() throws Exception {
        fail();
    }

    @Test
    public void testOperation_NotFound() throws Exception {
        fail();
    }

    @Test
    public void testHistory_Successful_OneHistory() throws Exception {
        fail();
    }

    @Test
    public void testHistory_Successful_MultiHistory() throws Exception {
        fail();
    }

    @Test
    public void testHistory_Successful_HistoryNotFound() throws Exception {
        fail();
    }

    @Test
    public void testHistory_NotFound() throws Exception {
        fail();
    }

    @Test
    public void testHistory_WithDomainAndAsset_Successful_OneHistory() throws Exception {
        fail();
    }

    @Test
    public void testHistory_WithDomainAndAsset_Successful_MultiHistory() throws Exception {
        fail();
    }

    @Test
    public void testHistory_WithDomainAndAsset_Successful_HistoryNotFound() throws Exception {
        fail();
    }

    @Test
    public void testHistory_WithDomainAndAsset_NotFound() throws Exception {
        fail();
    }

    @Test
    public void testSendMessage_Successful() throws Exception {
        fail();
    }

    @Test
    public void testSendMessage_Failure() throws Exception {
        fail();
    }

    @Test
    public void testSendMessage_NotFound() throws Exception {
        fail();
    }
}
