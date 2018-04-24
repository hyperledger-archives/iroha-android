package jp.co.soramitsu.iroha.android.sample.main.history;

public interface HistoryView {

    void finishRefresh();

    void didError(Throwable error);

}
