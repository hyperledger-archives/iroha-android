package jp.co.soramitsu.iroha.android.sample.main.send;

import android.content.Intent;
import javax.inject.Inject;

import jp.co.soramitsu.iroha.android.sample.R;
import jp.co.soramitsu.iroha.android.sample.SampleApplication;
import jp.co.soramitsu.iroha.android.sample.interactor.GetAccountInteractor;
import jp.co.soramitsu.iroha.android.sample.interactor.SendAssetInteractor;
import jp.co.soramitsu.iroha.android.sample.qrscanner.QRScannerActivity;
import lombok.Setter;

public class SendPresenter {

    @Setter
    private SendFragment fragment;

    private final SendAssetInteractor sendAssetInteractor;
    private final GetAccountInteractor getAccountInteractor;

    public static final int REQUEST_CODE_QR_SCAN = 101;

    @Inject
    public SendPresenter(SendAssetInteractor sendAssetInteractor, GetAccountInteractor getAccountInteractor) {
        this.sendAssetInteractor = sendAssetInteractor;
        this.getAccountInteractor = getAccountInteractor;
    }

    void sendTransaction(String username, String amount) {
        String[] data = {username, amount};
        if (!username.isEmpty() && !amount.isEmpty()) {
            if (SampleApplication.instance.account != null) {
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
                fragment.didSendError(new Throwable(SampleApplication.instance.getString(R.string.server_is_not_reachable)));
            }
        } else {
            fragment.didSendError(new Throwable(SampleApplication.instance.getString(R.string.fields_cant_be_empty)));
        }
    }

    private boolean isEnoughBalance(long amount) {
        return SampleApplication.instance.account.getBalance() >= amount;
    }

    public void doScanQr() {
        Intent i = new Intent(fragment.getActivity(), QRScannerActivity.class);
        fragment.startActivityForResult(i, REQUEST_CODE_QR_SCAN);
    }

}