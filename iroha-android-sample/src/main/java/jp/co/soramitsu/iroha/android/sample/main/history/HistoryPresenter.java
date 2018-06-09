package jp.co.soramitsu.iroha.android.sample.main.history;

import android.arch.lifecycle.ViewModelProviders;
import android.text.format.DateUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import jp.co.soramitsu.iroha.android.sample.interactor.GetAccountTransactionsInteractor;
import lombok.Setter;

public class HistoryPresenter {

    @Setter
    private HistoryFragment fragment;

    private final GetAccountTransactionsInteractor getAccountTransactionsInteractor;

    private TransactionsViewModel transactionsViewModel;

    @Inject
    public HistoryPresenter(GetAccountTransactionsInteractor getAccountTransactionsInteractor) {
        this.getAccountTransactionsInteractor = getAccountTransactionsInteractor;
    }

    void onCreateView() {
        transactionsViewModel = ViewModelProviders.of(fragment).get(TransactionsViewModel.class);
    }

    void getTransactions() {
        getAccountTransactionsInteractor.execute(
                transactions -> {
                    Collections.sort(transactions, (o1, o2) -> o2.date.compareTo(o1.date));
                    transactionsViewModel.getTransactions().postValue(transformTransactions(transactions));
                    fragment.finishRefresh();
                },
                throwable -> fragment.didError(throwable));
    }

    private List transformTransactions(List<Transaction> transactions) {
        if (transactions.isEmpty()) {
            return Collections.emptyList();
        }
        List listItems = new ArrayList();

        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        Date today = c.getTime();

        c.add(Calendar.DAY_OF_MONTH, -1);
        Date yesterday = c.getTime();

        c = Calendar.getInstance();
        c.set(Calendar.HOUR, -1);
        Date oneHourBefore = c.getTime();

        SimpleDateFormat headerDateFormat = new SimpleDateFormat("MMMM, dd", Locale.getDefault());
        SimpleDateFormat hoursDateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        String currentPrettyDate = getHeader(transactions.get(0).date, headerDateFormat,
                today, yesterday);

        listItems.add(currentPrettyDate);

        for (Transaction transaction : transactions) {
            if (!getHeader(transaction.date, headerDateFormat, today, yesterday)
                    .equals(currentPrettyDate)) {
                currentPrettyDate = getHeader(transaction.date, headerDateFormat,
                        today, yesterday);
                listItems.add(currentPrettyDate);
            }

            BigDecimal amount = new BigDecimal(transaction.amount);
            String prettyAmount = amount.toString();

            String prettyDate;
            if (currentPrettyDate.equals("Today") && transaction.date.after(oneHourBefore)) {
                long duration = new Date().getTime() - transaction.date.getTime();
                long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration);
                if (diffInMinutes == 0) {
                    prettyDate = "just now";
                } else {
                    prettyDate = diffInMinutes + " minutes ago";
                }
            } else {
                prettyDate = hoursDateFormat.format(transaction.date);
            }


            TransactionVM vm = new TransactionVM(transaction.id, prettyDate, transaction.username, prettyAmount);

            listItems.add(vm);
        }
        return listItems;
    }

    private String getHeader(Date date, SimpleDateFormat dateFormat, Date today, Date yesterday) {
        if (DateUtils.isToday(date.getTime())) {
            return "Today";
        } else if (date.before(today) && date.after(yesterday)) {
            return "Yesterday";
        } else {
            return dateFormat.format(date);
        }
    }

    void onStop() {
        fragment = null;
        getAccountTransactionsInteractor.unsubscribe();
    }
}