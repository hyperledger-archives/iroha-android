package jp.co.soramitsu.iroha.android.sample.main.receive;

import javax.inject.Inject;

import jp.co.soramitsu.iroha.android.sample.R;
import jp.co.soramitsu.iroha.android.sample.SampleApplication;
import jp.co.soramitsu.iroha.android.sample.interactor.GenerateQRInteractor;
import jp.co.soramitsu.iroha.android.sample.interactor.GetAccountInteractor;
import jp.co.soramitsu.iroha.android.sample.interactor.SendAssetInteractor;
import jp.co.soramitsu.iroha.android.sample.main.send.SendFragment;
import lombok.Setter;

public class ReceivePresenter {

    @Setter
    private ReceiveFragment fragment;

    private final GenerateQRInteractor generateQRInteractor;

    @Inject
    public ReceivePresenter(GenerateQRInteractor generateQRInteractor) {
        this.generateQRInteractor = generateQRInteractor;
    }

    void generateQR(String amount) {
        if (!amount.isEmpty()) {
            generateQRInteractor.execute(amount, bitmap -> {fragment.didGenerateSuccess(bitmap);}, throwable -> {});
        } else {
            fragment.resetQR();
        }
    }
}