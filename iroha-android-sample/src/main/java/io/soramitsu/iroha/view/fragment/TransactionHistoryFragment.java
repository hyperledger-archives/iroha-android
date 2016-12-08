package io.soramitsu.iroha.view.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import io.soramitsu.iroha.R;
import io.soramitsu.iroha.databinding.FragmentTransactionHistoryBinding;
import io.soramitsu.iroha.model.TransactionHistory;
import io.soramitsu.iroha.presenter.TransactionHistoryPresenter;
import io.soramitsu.iroha.view.TransactionHistoryView;
import io.soramitsu.iroha.view.activity.MainActivity;
import io.soramitsu.iroha.view.adapter.TransactionListAdapter;
import io.soramitsu.irohaandroid.model.KeyPair;
import io.soramitsu.irohaandroid.model.Transaction;

public class TransactionHistoryFragment extends Fragment implements TransactionHistoryView, MainActivity.MainActivityListener {
    public static final String TAG = TransactionHistoryFragment.class.getSimpleName();

    private TransactionHistoryPresenter transactionHistoryPresenter = new TransactionHistoryPresenter();

    private FragmentTransactionHistoryBinding binding;
    private TransactionListAdapter transactionListAdapter;

    private TransactionHistory transactionHistory;

//    public interface TransactionHistoryListener {
//        TransactionHistory getTransaction();
//        void renderTransactionHistory(TransactionHistory transactionHistory);
//    }

    public enum RefreshState {
        SWIPE_UP, RE_CREATED_FRAGMENT, EMPTY_REFRESH
    }

    public static TransactionHistoryFragment newInstance() {
        TransactionHistoryFragment fragment = new TransactionHistoryFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        transactionHistoryPresenter.setView(this);
        transactionHistoryPresenter.onCreate();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transaction_history, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = DataBindingUtil.bind(view);
        binding.swipeRefresh.setColorSchemeResources(R.color.red600, R.color.green600, R.color.blue600, R.color.orange600);
        binding.swipeRefresh.setOnRefreshListener(transactionHistoryPresenter.onSwipeRefresh());
        binding.transactionList.setOnScrollListener(transactionHistoryPresenter.onTransactionListScroll());
        binding.transactionList.setEmptyView(binding.emptyView);
        binding.emptyText.setOnClickListener(transactionHistoryPresenter.onRefresh());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        if (!(getActivity() instanceof TransactionHistoryListener)) {
//            throw new ClassCastException();
//        }

        transactionListAdapter = new TransactionListAdapter(
                getContext(),
                Transaction.createMock(), // TODO mock用
                KeyPair.getKeyPair(getContext()).publicKey
        );
        binding.transactionList.setAdapter(transactionListAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        transactionHistoryPresenter.onStart();
        transactionHistoryPresenter.transactionHistory(transactionHistory, RefreshState.RE_CREATED_FRAGMENT);
    }

    @Override
    public void onPause() {
        super.onPause();
        transactionHistoryPresenter.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        transactionHistoryPresenter.onDestroy();
    }

    @Override
    public boolean isRefreshing() {
        return binding.swipeRefresh.isRefreshing();
    }

    @Override
    public void setRefreshing(final boolean refreshing) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.swipeRefresh.setRefreshing(refreshing);
            }
        });
    }

    @Override
    public void setRefreshEnable(final boolean enable) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.swipeRefresh.setEnabled(enable);
            }
        });
    }

    @Override
    public void showError(final String error) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public TransactionHistory getTransaction() {
        return transactionHistory;
    }

    @Override
    public void renderTransactionHistory(final TransactionHistory transactionHistory) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TransactionHistoryFragment.this.transactionHistory = transactionHistory;
                binding.pocketMoney.setText(TransactionHistoryFragment.this.transactionHistory.value);
                transactionListAdapter.setItems(TransactionHistoryFragment.this.transactionHistory.histories);
                transactionListAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void showProgressDialog() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.transactionList.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void hideProgressDialog() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.progressBar.setVisibility(View.GONE);
                binding.transactionList.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onNavigationItemClicked() {
        binding.transactionList.setSelection(0);
    }
}
