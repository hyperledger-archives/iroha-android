package jp.co.soramitsu.iroha.android.sample.main;


import javax.inject.Inject;

import jp.co.soramitsu.iroha.android.sample.PreferencesUtil;
import jp.co.soramitsu.iroha.android.sample.interactor.GetAccountDetailsInteractor;
import jp.co.soramitsu.iroha.android.sample.interactor.SetAccountDetailsInteractor;
import lombok.Setter;

public class MainPresenter {

    private final PreferencesUtil preferencesUtil;
    private final SetAccountDetailsInteractor setAccountDetails;
    private final GetAccountDetailsInteractor getAccountDetails;

    @Setter
    private MainView view;

    @Inject
    public MainPresenter(PreferencesUtil preferencesUtil,
                         SetAccountDetailsInteractor setAccountDetails,
                         GetAccountDetailsInteractor getAccountDetails) {
        this.preferencesUtil = preferencesUtil;
        this.setAccountDetails = setAccountDetails;
        this.getAccountDetails = getAccountDetails;
    }

    void onCreate() {
        view.setUsername(preferencesUtil.retrieveUsername());
        getAccountDetails.execute(details -> {
            view.setAccountDetails(details);
        }, throwable -> {
        });
    }

    void logout() {
        preferencesUtil.clear();
        view.showRegistrationScreen();
    }

    void setAccountDetails(String details) {
        view.showProgress();
        setAccountDetails.execute(details, () -> {
            view.hideProgress();
            view.setAccountDetails(details);
        }, throwable -> {
            view.showSetDetailsAccountError();
            view.hideProgress();
        });
    }
}