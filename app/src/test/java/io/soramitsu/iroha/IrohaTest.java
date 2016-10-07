package io.soramitsu.iroha;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.List;

import io.soramitsu.iroha.models.Asset;
import io.soramitsu.iroha.models.Domain;
import io.soramitsu.iroha.models.IrohaUser;
import io.soramitsu.iroha.models.apis.IrohaUserClient;
import io.soramitsu.iroha.models.apis.TransactionClient;

import static io.soramitsu.iroha.utils.DigestUtil.createKeyPair;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.reflect.Whitebox.getConstructor;
import static org.powermock.reflect.Whitebox.setInternalState;


@RunWith(PowerMockRunner.class)
public class IrohaTest extends TestCase {
    @InjectMocks
    private Iroha iroha;

    @Mock
    private IrohaUserClient userClient;

    @Mock
    private TransactionClient transactionClient;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        Constructor<Iroha> constructor = getConstructor(Iroha.class, IrohaUserClient.class, TransactionClient.class);
        iroha = constructor.newInstance(userClient, transactionClient);
        setInternalState(iroha, "endpoint", "http://iroha.tech");
    }

    @Test
    public void testBuilder_Successful() throws Exception {
        iroha = new Iroha.Builder().baseUrl("http://iroha.tech").build();

        final Class c = iroha.getClass();
        final Field field = c.getDeclaredField("endpoint");
        field.setAccessible(true);
        final String endpoint = (String) field.get(iroha);

        assertThat(endpoint, is("http://iroha.tech"));
    }

    @Test
    public void testBuilder_Throw_NullPointerException() throws Exception {
        thrown.expect(NullPointerException.class);
        iroha = new Iroha.Builder().build();
    }

    @Test
    public void testGetInstance_Successful() throws Exception {
        iroha = Iroha.getInstance();
        assertThat(iroha, notNullValue());
    }

    @Test
    public void testUserRegister_Successful() throws Exception {
        final String endpoint = "http://iroha.tech";
        final PublicKey publicKey = createKeyPair().getPublic();
        final String name = "user_name";
        final String uuid = "1234567890";
        final IrohaUser user = new IrohaUser();
        user.setStatus(200);
        user.setName(name);
        user.setUuid(uuid);

        when(userClient.register(endpoint, publicKey, name)).thenReturn(user);

        IrohaUser result = iroha.register(publicKey, name);

        assertThat(result.getStatus(), is(200));
        assertThat(result.getName(), is(name));
        assertThat(result.getUuid(), is(uuid));

        verify(userClient).register(endpoint, publicKey, name);
        verifyNoMoreInteractions(userClient);
    }

    @Test
    public void testGetAccountInfo_Successful() throws Exception {
        final String endpoint = "http://iroha.tech";
        final String name = "user_name";
        final String uuid = "1234567890";
        final String assetName = "asset_name";
        final String domainName = "domain_name";
        final String assetValue = "100";
        final Asset asset1 = new Asset();
        asset1.setName(assetName);
        asset1.setDomain(domainName);
        asset1.setValue(assetValue);
        final Asset asset2 = new Asset();
        asset2.setName(assetName);
        asset2.setDomain(domainName);
        asset2.setValue(assetValue);
        List<Asset> assets = Arrays.asList(asset1, asset2);
        final IrohaUser user = new IrohaUser();
        user.setStatus(200);
        user.setName(name);
        user.setUuid(uuid);
        user.setAssets(assets);

        when(userClient.findAccountInfo(endpoint, uuid)).thenReturn(user);

        IrohaUser result = iroha.getAccountInfo(uuid);

        assertThat(result.getStatus(), is(200));
        assertThat(result.getName(), is(name));
        assertThat(result.getUuid(), is(uuid));
        assertThat(result.getAssets().size(), is(2));
        assertThat(result.getAssets().get(0).getName(), is(assetName));
        assertThat(result.getAssets().get(0).getDomain(), is(domainName));
        assertThat(result.getAssets().get(0).getValue(), is(assetValue));

        verify(userClient).findAccountInfo(endpoint, uuid);
        verifyNoMoreInteractions(userClient);
    }

    @Test
    public void testRegisterDomain_Successful() throws Exception {
        final String endpoint = "http://iroha.tech";
        final String name = "domain_name";
        final KeyPair keyPair = createKeyPair();
        final Domain domain = new Domain();
        domain.setName(name);
        domain.setStatus(200);
        domain.setMessage("Domain registered successfully.");

        when(transactionClient.registerDomain(endpoint, name, keyPair)).thenReturn(domain);

        Domain result = iroha.registerDomain(name, keyPair);

        assertThat(result.getStatus(), is(200));
        assertThat(result.getMessage(), is("Domain registered successfully."));

        verify(transactionClient).registerDomain(endpoint, name, keyPair);
        verifyNoMoreInteractions(transactionClient);
    }

    @Test
    public void testCreateAsset_Successful() throws Exception {
        final String endpoint = "http://iroha.tech";
        final String name = "asset_name";
        final String domainName = "domain_name";
        final KeyPair keyPair = createKeyPair();
        final Asset asset = new Asset();
        asset.setName(name);
        asset.setStatus(200);
        asset.setMessage("Asset created successfully.");

        when(transactionClient.registerAsset(endpoint, name, domainName, keyPair)).thenReturn(asset);

        Asset result = iroha.createAsset(name, domainName, keyPair);

        assertThat(result.getStatus(), is(200));
        assertThat(result.getMessage(), is("Asset created successfully."));

        verify(transactionClient).registerAsset(endpoint, name, domainName, keyPair);
        verifyNoMoreInteractions(transactionClient);
    }

    @Test
    public void testGetDomains_Successful() throws Exception {
        final String endpoint = "http://iroha.tech";
        final String name = "domain_name";
        final String creator = "creator";
        final String signature = "signature";
        final Domain domain1 = new Domain();
        domain1.setName(name);
        domain1.setCreator(creator);
        final Domain domain2 = new Domain();
        domain2.setName(name);
        domain2.setCreator(creator);
        List<Domain> domains = Arrays.asList(domain1, domain2);

        when(transactionClient.findDomains(endpoint)).thenReturn(domains);

        List<Domain> result = iroha.getDomains();

        assertThat(result.size(), is(2));
        assertThat(result.get(0).getName(), is(name));

        verify(transactionClient).findDomains(endpoint);
        verifyNoMoreInteractions(transactionClient);
    }

    @Test
    public void testGetAssets_Successful() throws Exception {
        final String endpoint = "http://iroha.tech";
        final String name = "asset_name";
        final String domainName = "domain_name";
        final String creator = "creator";
        final Asset asset1 = new Asset();
        asset1.setName(name);
        asset1.setCreator(creator);
        final Asset asset2 = new Asset();
        asset2.setName(name);
        asset2.setCreator(creator);
        List<Asset> assets = Arrays.asList(asset1, asset2);

        when(transactionClient.findAssets(endpoint, domainName)).thenReturn(assets);

        List<Asset> result = iroha.getAssets(domainName);

        assertThat(result.size(), is(2));
        assertThat(result.get(0).getName(), is(name));

        verify(transactionClient).findAssets(endpoint, domainName);
        verifyNoMoreInteractions(transactionClient);
    }
}
