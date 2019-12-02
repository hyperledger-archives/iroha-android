package jp.co.soramitsu.irohaandroid.presentation.main.history

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import jp.co.soramitsu.irohaandroid.R
import jp.co.soramitsu.irohaandroid.presentation.model.TransactionModel
import jp.co.soramitsu.irohaandroid.util.Const

class HistoryAdapter(private val context: Context) : ListAdapter<TransactionModel, TransactionViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): TransactionViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_transaction, viewGroup, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(transactionViewHolder: TransactionViewHolder, position: Int) {
        transactionViewHolder.bind(getItem(position))
    }
}

class TransactionViewHolder(
    itemView: View
) : RecyclerView.ViewHolder(itemView) {

    private val username: TextView = itemView.findViewById(R.id.username)
    private val date: TextView = itemView.findViewById(R.id.date)
    private val amount: TextView = itemView.findViewById(R.id.amount)

    fun bind(transaction: TransactionModel) {
        username.text = transaction.username
        date.text = transaction.prettyDate
        amount.text = "${transaction.prettyAmount} ${Const.ASSET_NAME}"
    }
}

object DiffCallback : DiffUtil.ItemCallback<TransactionModel>() {
    override fun areItemsTheSame(oldItem: TransactionModel, newItem: TransactionModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: TransactionModel, newItem: TransactionModel): Boolean {
        return oldItem == newItem
    }
}