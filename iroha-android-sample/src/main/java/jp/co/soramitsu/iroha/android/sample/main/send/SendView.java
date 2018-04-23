package jp.co.soramitsu.iroha.android.sample.main.send;

public interface SendView {

    void didSendSuccess();

    void didSendError(Throwable error);

}
