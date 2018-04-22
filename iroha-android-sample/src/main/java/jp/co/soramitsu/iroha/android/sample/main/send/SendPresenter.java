package jp.co.soramitsu.iroha.android.sample.main.send;

import javax.inject.Inject;

import jp.co.soramitsu.iroha.android.sample.R;
import jp.co.soramitsu.iroha.android.sample.SampleApplication;
import jp.co.soramitsu.iroha.android.sample.interactor.GetAccountInteractor;
import jp.co.soramitsu.iroha.android.sample.interactor.SendAssetInteractor;
import lombok.Setter;

public class SendPresenter {

    @Setter
    private SendFragment fragment;

    private final SendAssetInteractor sendAssetInteractor;
    private final GetAccountInteractor getAccountInteractor;

    @Inject
    public SendPresenter(SendAssetInteractor sendAssetInteractor, GetAccountInteractor getAccountInteractor) {
        this.sendAssetInteractor = sendAssetInteractor;
        this.getAccountInteractor = getAccountInteractor;
    }

    void sendTransaction(String username, String amount) {
        String[] data = {username, amount};
        if (!username.isEmpty() && !amount.isEmpty()) {
            if (!isEnoughBalance(Long.parseLong(amount))) {
                fragment.didSendError(new Throwable(fragment.getString(R.string.not_enough_balance_error)));
            } else {
                getAccountInteractor.execute(username,
                        account -> {
                            if (account.getAccountId().isEmpty()) {
                                fragment.didSendError(new Throwable(SampleApplication.instance.getString(R.string.username_doesnt_exists)));
                            } else {
                                sendAssetInteractor.execute(data,
                                        () -> fragment.didSendSuccess(),
                                        error -> fragment.didSendError(error)
                                );
                            }
                        }, throwable -> fragment.didSendError(throwable));

            }
        } else {
            fragment.didSendError(new Throwable(SampleApplication.instance.getString(R.string.fields_cant_be_empty)));
        }
    }

    private boolean isEnoughBalance(long amount) {
        return SampleApplication.instance.account.getBalance() >= amount;
    }

}