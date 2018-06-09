package jp.co.soramitsu.iroha.android.sample.main.receive;

import com.orhanobut.logger.Logger;

import javax.inject.Inject;

import jp.co.soramitsu.iroha.android.sample.interactor.GenerateQRInteractor;
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
        if (amount.isEmpty()) {
            amount = "0";
        }
        generateQRInteractor.execute(amount, bitmap -> {
            fragment.didGenerateSuccess(bitmap);
        }, throwable -> {
            Logger.e(throwable.getMessage());
        });
    }

    void onStop() {
        fragment = null;
        generateQRInteractor.unsubscribe();
    }
}