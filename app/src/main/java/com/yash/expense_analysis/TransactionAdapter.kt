package com.yash.expense_analysis

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.yash.expense_analysis.model.Transactions
import com.yash.expense_analysis.utils.Constants.EDIT_TAG
import kotlinx.android.synthetic.main.list_transactions.view.*
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Joshi on 26-07-2020.
 */
class TransactionAdapter(val listener: (Pair<Transactions, String>) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    var mList: List<Transactions> = arrayListOf()
    var tempList: List<Transactions> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_transactions, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = mList.size

    override fun getFilter(): Filter = ValueFilter()

    override fun getItemViewType(position: Int) = position

    override fun getItemId(position: Int) = position.toLong()

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bindItems(mList[position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(transactions: Transactions) = with(itemView) {

            tvMessages.text = transactions.messageBody
            tvTransactionAmount.text =
                context.getString(R.string.rupee_symbol) + transactions.totalAmount
            tvTransactionType.text = transactions.transactionType

            if (transactions.tag.isNullOrEmpty()) {
                llEditTag.visibility = View.VISIBLE
                tvMessagesTag.visibility = View.GONE
            } else {
                llEditTag.visibility = View.GONE

                tvMessagesTag.visibility = View.VISIBLE
                tvMessagesTag.text = transactions.tag
            }

            btnSubmit.setOnClickListener {
                val tag = etTag.text.toString().trim()
                if (tag.isEmpty()) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.invalid_tag_error), Toast.LENGTH_LONG
                    )
                        .show()
                    return@setOnClickListener
                }

                transactions.tag = tag
                listener(Pair(transactions, EDIT_TAG))
            }
        }
    }

    inner class ValueFilter : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            mList = tempList
            val results = FilterResults()
            if (constraint.isNotEmpty()) {
                val filterList = ArrayList<Transactions>()
                for (transaction in mList) {
                    val tag = transaction.tag ?: ""
                    if (tag.toLowerCase(Locale.ENGLISH).contains(
                            constraint.toString().toLowerCase(
                                Locale.ENGLISH
                            )
                        )
                    ) filterList.add(transaction)
                }
                results.count = filterList.size
                results.values = filterList
            } else {
                results.count = mList.size
                results.values = mList
            }
            return results
        }

        override fun publishResults(p0: CharSequence?, results: FilterResults?) {
            if (results != null) {
                val values = results.values
                if (values != null) {
                    mList = values as List<Transactions>
                    notifyDataSetChanged()
                }
            }
        }
    }

}