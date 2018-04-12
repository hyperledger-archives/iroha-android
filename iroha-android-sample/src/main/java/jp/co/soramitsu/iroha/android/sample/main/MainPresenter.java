package jp.co.soramitsu.iroha.android.sample.main;


import javax.inject.Inject;

import jp.co.soramitsu.iroha.android.sample.PreferencesUtil;
import jp.co.soramitsu.iroha.android.sample.SampleApplication;
import jp.co.soramitsu.iroha.android.sample.interactor.GetAccountBalanceInteractor;
import jp.co.soramitsu.iroha.android.sample.interactor.GetAccountDetailsInteractor;
import jp.co.soramitsu.iroha.android.sample.interactor.GetAccountInteractor;
import jp.co.soramitsu.iroha.android.sample.interactor.SetAccountDetailsInteractor;
import lombok.Setter;

public class MainPresenter {

    private final PreferencesUtil preferencesUtil;
    private final SetAccountDetailsInteractor setAccountDetails;
    private final GetAccountDetailsInteractor getAccountDetails;
    private final GetAccountInteractor getAccountInteractor;
    private final GetAccountBalanceInteractor getAccountBalanceInteractor;

    @Setter
    private MainView view;

    @Inject
    public MainPresenter(PreferencesUtil preferencesUtil,
                         SetAccountDetailsInteractor setAccountDetails,
                         GetAccountDetailsInteractor getAccountDetails,
                         GetAccountInteractor getAccountInteractor,
                         GetAccountBalanceInteractor getAccountBalanceInteractor) {
        this.preferencesUtil = preferencesUtil;
        this.setAccountDetails = setAccountDetails;
        this.getAccountDetails = getAccountDetails;
        this.getAccountInteractor = getAccountInteractor;
        this.getAccountBalanceInteractor = getAccountBalanceInteractor;

    }

    void onCreate() {
        view.setUsername(preferencesUtil.retrieveUsername());

        getAccountInteractor.execute(
                account -> SampleApplication.instance.account = account,
                throwable -> {});

        updateData(false);

        getAccountDetails.execute(details -> {
                view.setAccountDetails(details);
            }, throwable -> {
        });
    }


    void updateData(boolean fromRefresh) {
        getAccountBalanceInteractor.execute(
                balance -> {
                    if (fromRefresh) {
                        view.hideRefresh();
                    }
                    view.setAccountBalance(balance);
                },
                throwable -> view.showError(throwable));
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
            view.showError(throwable);
            view.hideProgress();
        });
    }
}