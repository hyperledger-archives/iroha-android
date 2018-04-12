package jp.co.soramitsu.iroha.android.sample.main.send;

import com.orhanobut.logger.Logger;

import javax.inject.Inject;

import jp.co.soramitsu.iroha.android.sample.interactor.SendAssetInteractor;
import lombok.Setter;

public class SendPresenter {

    @Setter
    private SendFragment fragment;

    private final SendAssetInteractor sendAssetInteractor;

    @Inject
    public SendPresenter(SendAssetInteractor sendAssetInteractor) {
        this.sendAssetInteractor = sendAssetInteractor;
    }

    void sendTransaction(String username, String amount) {
        String[] data = {username, amount};
        sendAssetInteractor.execute(data,
                () -> {
                    Logger.e("Success");
                },
                error -> {
                    Logger.e("error" + error.getLocalizedMessage());
                });
    }

}