package io.soramitsu.irohaandroid;

import android.content.Context;

import org.bouncycastle.jcajce.provider.digest.SHA3;

import java.security.MessageDigest;
import java.util.List;

import io.soramitsu.irohaandroid.data.executor.JobExecutor;
import io.soramitsu.irohaandroid.data.repository.AccountDataRepository;
import io.soramitsu.irohaandroid.data.repository.AssetDataRepository;
import io.soramitsu.irohaandroid.data.repository.DomainDataRepository;
import io.soramitsu.irohaandroid.data.repository.KeyPairDataRepository;
import io.soramitsu.irohaandroid.data.repository.TransactionDataRepository;
import io.soramitsu.irohaandroid.domain.entity.Account;
import io.soramitsu.irohaandroid.domain.entity.Asset;
import io.soramitsu.irohaandroid.domain.entity.Domain;
import io.soramitsu.irohaandroid.domain.entity.KeyPair;
import io.soramitsu.irohaandroid.domain.entity.Transaction;
import io.soramitsu.irohaandroid.domain.entity.TransactionHistory;
import io.soramitsu.irohaandroid.domain.entity.reqest.AccountRegisterRequest;
import io.soramitsu.irohaandroid.domain.entity.reqest.AssetOperationRequest;
import io.soramitsu.irohaandroid.domain.entity.reqest.AssetRegisterRequest;
import io.soramitsu.irohaandroid.domain.entity.reqest.DomainRegisterRequest;
import io.soramitsu.irohaandroid.domain.interactor.DefaultSubscriber;
import io.soramitsu.irohaandroid.domain.interactor.DeleteKeyPairUseCase;
import io.soramitsu.irohaandroid.domain.interactor.FetchAccountUseCase;
import io.soramitsu.irohaandroid.domain.interactor.FetchAssetUseCase;
import io.soramitsu.irohaandroid.domain.interactor.FetchDomainUseCase;
import io.soramitsu.irohaandroid.domain.interactor.FetchKeyPairUseCase;
import io.soramitsu.irohaandroid.domain.interactor.FetchMultiAssetsTransactionUseCase;
import io.soramitsu.irohaandroid.domain.interactor.FetchTransactionHistoryUseCase;
import io.soramitsu.irohaandroid.domain.interactor.FetchTransactionUseCase;
import io.soramitsu.irohaandroid.domain.interactor.FetchUuidUseCase;
import io.soramitsu.irohaandroid.domain.interactor.OperationAssetUseCase;
import io.soramitsu.irohaandroid.domain.interactor.RegisterAccountUseCase;
import io.soramitsu.irohaandroid.domain.interactor.RegisterAssetUseCase;
import io.soramitsu.irohaandroid.domain.interactor.RegisterDomainUseCase;
import io.soramitsu.irohaandroid.domain.interactor.RegisterKeyPairUseCase;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.R.attr.key;

public class Iroha {
    private static Iroha iroha;

    private RegisterKeyPairUseCase registerKeyPairUseCase;
    private DeleteKeyPairUseCase deleteKeyPairUseCase;
    private FetchKeyPairUseCase fetchKeyPairUseCase;
    private FetchUuidUseCase fetchUuidUseCase;
    private RegisterAccountUseCase registerAccountUseCase;
    private FetchAccountUseCase fetchAccountUseCase;
    private RegisterDomainUseCase registerDomainUseCase;
    private FetchDomainUseCase fetchDomainUseCase;
    private RegisterAssetUseCase registerAssetUseCase;
    private FetchAssetUseCase fetchAssetUseCase;
    private OperationAssetUseCase operationAssetUseCase;
    private FetchTransactionUseCase fetchTransactionUseCase;
    private FetchMultiAssetsTransactionUseCase fetchMultiAssetsTransactionUseCase;
    private FetchTransactionHistoryUseCase fetchTransactionHistoryUseCase;

    private String baseUrl;

    private Iroha(Builder builder) {
        this.baseUrl = builder.baseUrl;
        iroha = this;
    }

    public static class Builder {
        private String baseUrl;

        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Iroha build() {
            if (baseUrl == null) {
                throw new NullPointerException();
            }
            return new Iroha(this);
        }
    }

    public static Iroha getInstance() {
        if (iroha == null) {
            throw new NullPointerException();
        }
        return iroha;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void generateKeyPair(Context context, Subscriber<Boolean> callback) {
        if (registerKeyPairUseCase == null) {
            registerKeyPairUseCase = new RegisterKeyPairUseCase(
                    AndroidSchedulers.mainThread(),
                    AndroidSchedulers.mainThread(),
                    KeyPairDataRepository.getInstance(context)
            );
        }

        registerKeyPairUseCase.execute(callback);
    }

    public void unsubscribeRegisterKeyPair() {
        if (registerKeyPairUseCase != null) {
            registerKeyPairUseCase.unsubscribe();
        }
    }

    public void removeKeyPair(Context context) {
        if (deleteKeyPairUseCase == null) {
            deleteKeyPairUseCase = new DeleteKeyPairUseCase(
                    AndroidSchedulers.mainThread(),
                    AndroidSchedulers.mainThread(),
                    KeyPairDataRepository.getInstance(context)
            );
        }

        deleteKeyPairUseCase.execute(new DefaultSubscriber());
    }

    public void unsubscribeDeleteKeyPair() {
        if (deleteKeyPairUseCase != null) {
            deleteKeyPairUseCase.unsubscribe();
        }
    }

    public void findKeyPair(Context context, Subscriber<KeyPair> callback) {
        if (fetchKeyPairUseCase == null) {
            fetchKeyPairUseCase = new FetchKeyPairUseCase(
                    Schedulers.from(new JobExecutor()),
                    AndroidSchedulers.mainThread(),
                    KeyPairDataRepository.getInstance(context)
            );
        }

        fetchKeyPairUseCase.execute(callback);
    }

    public void unsubscribeFetchKeyPair() {
        if (fetchKeyPairUseCase != null) {
            fetchKeyPairUseCase.unsubscribe();
        }
    }

    public String findUuid(Context context) {
        if (fetchUuidUseCase == null) {
            fetchUuidUseCase = new FetchUuidUseCase(
                    AndroidSchedulers.mainThread(),
                    AndroidSchedulers.mainThread(),
                    new AccountDataRepository(context)
            );
        }

        return fetchUuidUseCase.findUuid().toBlocking().first();
    }

    public void unsbscribeFindUuid() {
        if (fetchUuidUseCase != null) {
            fetchUuidUseCase.unsubscribe();
        }
    }

    public void registerAccount(Context context, AccountRegisterRequest body, Subscriber<Account> callback) {
        if (registerAccountUseCase == null) {
            registerAccountUseCase = new RegisterAccountUseCase(
                    Schedulers.from(new JobExecutor()),
                    AndroidSchedulers.mainThread(),
                    body,
                    new AccountDataRepository(context.getApplicationContext())
            );
        }

        registerAccountUseCase.execute(callback);
    }

    public void unsubscribeRegisterAccount() {
        if (registerAccountUseCase != null) {
            registerAccountUseCase.unsubscribe();
        }
    }

    public void findAccountInfo(Context context, String uuid, Subscriber<Account> callback) {
        if (fetchAccountUseCase == null) {
            fetchAccountUseCase = new FetchAccountUseCase(
                    Schedulers.from(new JobExecutor()),
                    AndroidSchedulers.mainThread(),
                    uuid,
                    new AccountDataRepository(context.getApplicationContext())
            );
        }

        fetchAccountUseCase.execute(callback);
    }

    public void unsubscribeFindAccount() {
        if (fetchAccountUseCase != null) {
            fetchAccountUseCase.unsubscribe();
        }
    }

    public void registerDomain(DomainRegisterRequest body, Subscriber<Domain> callback) {
        if (registerDomainUseCase == null) {
            registerDomainUseCase = new RegisterDomainUseCase(
                    Schedulers.from(new JobExecutor()),
                    AndroidSchedulers.mainThread(),
                    body,
                    new DomainDataRepository()
            );
        }

        registerAccountUseCase.execute(callback);
    }

    public void unsubscribeRegisterDomain() {
        if (registerDomainUseCase != null) {
            registerAccountUseCase.unsubscribe();
        }
    }

    public void findDomains(Subscriber<List<Domain>> callback) {
        if (fetchDomainUseCase == null) {
            fetchDomainUseCase = new FetchDomainUseCase(
                    Schedulers.from(new JobExecutor()),
                    AndroidSchedulers.mainThread(),
                    new DomainDataRepository()
            );
        }

        fetchDomainUseCase.execute(callback);
    }

    public void unsubscribeFindDomains() {
        if (fetchAccountUseCase != null) {
            fetchAccountUseCase.unsubscribe();
        }
    }

    public void createAsset(AssetRegisterRequest body, Subscriber<Asset> callback) {
        if (registerAssetUseCase == null) {
            registerAssetUseCase = new RegisterAssetUseCase(
                    Schedulers.from(new JobExecutor()),
                    AndroidSchedulers.mainThread(),
                    body,
                    new AssetDataRepository()
            );
        }

        registerAccountUseCase.execute(callback);
    }

    public void unsubscribeCreateAsset() {
        if (registerAssetUseCase != null) {
            registerAssetUseCase.unsubscribe();
        }
    }

    public void findAssets(String domain, Subscriber<List<Asset>> callback) {
        if (fetchAssetUseCase == null) {
            fetchAssetUseCase = new FetchAssetUseCase(
                    Schedulers.from(new JobExecutor()),
                    AndroidSchedulers.mainThread(),
                    domain,
                    new AssetDataRepository()
            );
        }

        fetchAssetUseCase.execute(callback);
    }

    public void unsubscribeFindAssets() {
        if (fetchAssetUseCase != null) {
            fetchAssetUseCase.unsubscribe();
        }
    }

    public void operationAsset(AssetOperationRequest body, Subscriber<Asset> callback) {
        if (operationAssetUseCase == null) {
            operationAssetUseCase = new OperationAssetUseCase(
                    Schedulers.from(new JobExecutor()),
                    AndroidSchedulers.mainThread(),
                    body,
                    new AssetDataRepository()
            );
        }

        operationAssetUseCase.execute(callback);
    }

    public void unsubscribeOperationAsset() {
        if (operationAssetUseCase != null) {
            operationAssetUseCase.unsubscribe();
        }
    }

    public void findTransactionHistory(Context context, Subscriber<TransactionHistory> callback) {
        if (fetchTransactionHistoryUseCase == null) {
            fetchTransactionHistoryUseCase = new FetchTransactionHistoryUseCase(
                    Schedulers.from(new JobExecutor()),
                    AndroidSchedulers.mainThread(),
                    new AccountDataRepository(context),
                    new TransactionDataRepository(context)
            );
        }

        fetchTransactionHistoryUseCase.execute(callback);
    }

    public void unsubscribeFindTransactionHistory() {
        if (fetchTransactionHistoryUseCase != null) {
            fetchTransactionHistoryUseCase.unsubscribe();
        }
    }

    public void findTransactionHistory(Context context, String uuid, String domain, String asset, Subscriber<TransactionHistory> callback) {
        if (fetchMultiAssetsTransactionUseCase == null) {
            fetchMultiAssetsTransactionUseCase = new FetchMultiAssetsTransactionUseCase(
                    Schedulers.from(new JobExecutor()),
                    AndroidSchedulers.mainThread(),
                    uuid,
                    domain,
                    asset,
                    new TransactionDataRepository(context)
            );
        }

        fetchMultiAssetsTransactionUseCase.execute(callback);
    }

    public void unsubscribeFindMultAssetsTransactionHistory() {
        if (fetchMultiAssetsTransactionUseCase != null) {
            fetchMultiAssetsTransactionUseCase.unsubscribe();
        }
    }

    public static KeyPair createKeyPair() {
        return Ed25519.createKeyPair();
    }

    public static String sign(KeyPair keyPair, String message) {
        return Ed25519.sign(message, keyPair);
    }

    public static boolean verify(String publicKey, String signature, String message) {
        return Ed25519.verify(signature, message, publicKey);
    }

    public static String sha3_256(final String message) {
        return sha3_x(new SHA3.Digest256(), message);
    }

    public static String sha3_384(final String message) {
        return sha3_x(new SHA3.Digest384(), message);
    }

    public static String sha3_512(final String message) {
        return sha3_x(new SHA3.Digest512(), message);
    }

    private static String sha3_x(SHA3.DigestSHA3 sha3, String message) {
        sha3.update(message.getBytes());
        return hashToString(sha3);
    }

    private static String hashToString(MessageDigest hash) {
        return hashToString(hash.digest());
    }

    private static String hashToString(byte[] hash) {
        StringBuilder buff = new StringBuilder();

        for (byte b : hash) {
            buff.append(String.format("%02x", b & 0xFF));
        }

        return buff.toString();
    }
}
