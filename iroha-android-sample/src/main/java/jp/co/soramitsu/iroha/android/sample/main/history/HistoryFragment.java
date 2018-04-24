package jp.co.soramitsu.iroha.android.sample.main.history;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.orhanobut.logger.Logger;

import javax.inject.Inject;

import jp.co.soramitsu.iroha.android.sample.R;
import jp.co.soramitsu.iroha.android.sample.SampleApplication;
import jp.co.soramitsu.iroha.android.sample.databinding.FragmentHistoryBinding;
import jp.co.soramitsu.iroha.android.sample.main.MainActivity;

public class HistoryFragment extends Fragment implements HistoryView {
    private FragmentHistoryBinding binding;

    @Inject
    HistoryPresenter presenter;

    private TransactionsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_history, container, false);
        SampleApplication.instance.getApplicationComponent().inject(this);

        presenter.setFragment(this);
        presenter.onCreateView();
        presenter.getTransactoins();

        TransactionsViewModel transactionsViewModel = ViewModelProviders.of(this).get(TransactionsViewModel.class);
        transactionsViewModel.getTransactions().observe(this, transactions -> {
            DiffUtil.Callback transactionDiffChecker =
                    new TransactionDiffChecker(adapter.getTransactions(), transactions);
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(transactionDiffChecker);
            adapter.setTransactions(transactions);
            diffResult.dispatchUpdatesTo(adapter);
        });

        configureRecycler();
        binding.refresh.setOnRefreshListener(() -> presenter.getTransactoins());
        return binding.getRoot();
    }

    private void configureRecycler() {
        binding.transactions.setHasFixedSize(true);
        binding.transactions.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TransactionsAdapter();
        binding.transactions.setAdapter(adapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.onStop();
    }

    @Override
    public void finishRefresh() {
        binding.refresh.setRefreshing(false);
    }

    @Override
    public void didError(Throwable error) {
        ((MainActivity) getActivity()).showError(error);
    }
}