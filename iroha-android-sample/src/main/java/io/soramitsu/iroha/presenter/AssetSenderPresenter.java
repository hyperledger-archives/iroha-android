package io.soramitsu.iroha.presenter;

import android.support.annotation.NonNull;
import android.view.View;

import io.soramitsu.iroha.R;
import io.soramitsu.iroha.model.QRType;
import io.soramitsu.iroha.util.NetworkUtil;
import io.soramitsu.iroha.view.AssetSenderView;
import io.soramitsu.irohaandroid.Iroha;
import io.soramitsu.irohaandroid.domain.entity.Asset;
import rx.Subscriber;

public class AssetSenderPresenter implements Presenter<AssetSenderView> {

    private AssetSenderView assetSenderView;

    @Override
    public void setView(@NonNull AssetSenderView view) {
        assetSenderView = view;
    }

    @Override
    public void onCreate() {
        // nothing
    }

    @Override
    public void onStart() {
        // nothing
    }

    @Override
    public void onResume() {
        // nothing
    }

    @Override
    public void onPause() {
        // nothing
    }

    @Override
    public void onStop() {
        Iroha.getInstance().unsbscribeFindUuid();
        Iroha.getInstance().unsubscribeOperationAsset();
    }

    @Override
    public void onDestroy() {
        // nothing
    }

    public View.OnClickListener onSubmitClicked() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                send();
            }
        };
    }

    private void send() {
        assetSenderView.showProgressDialog();

        Iroha.getInstance().operationAsset(
                assetSenderView.getContext(),
                QRType.TRANSFER.getType(),
                assetSenderView.getReceiver(),
                assetSenderView.getAmount(),
                new Subscriber<Asset>() {
                    @Override
                    public void onCompleted() {
                        String successfulMessage = "";
                        successfulMessage += assetSenderView.getReceiver();
                        successfulMessage += "に";
                        successfulMessage += assetSenderView.getAmount();
                        successfulMessage += "の送金をしました";

                        assetSenderView.showSuccess(
                                assetSenderView.getContext().getString(R.string.successful_title_sent),
                                successfulMessage,
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        assetSenderView.hideSuccess();
                                        assetSenderView.beforeQRReadViewState();
                                    }
                                });
                    }

                    @Override
                    public void onError(Throwable e) {
                        assetSenderView.hideProgressDialog();

                        if (NetworkUtil.isOnline(assetSenderView.getContext())) {
                            assetSenderView.showError(assetSenderView.getContext().getString(R.string.error_message_retry_again));
                        } else {
                            assetSenderView.showError(assetSenderView.getContext().getString(R.string.error_message_check_network_state));
                        }
                    }

                    @Override
                    public void onNext(Asset asset) {
                        assetSenderView.hideProgressDialog();
                    }
                });
    }

    public View.OnClickListener onQRShowClicked() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                assetSenderView.showQRReader();
            }
        };
    }

    public View.OnClickListener onResetClicked() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                assetSenderView.reset();
                assetSenderView.beforeQRReadViewState();
            }
        };
    }

    public String getUuid() {
        return Iroha.getInstance().findUuid(assetSenderView.getContext());
    }
}
