package com.yash.expense_analysis

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.yash.expense_analysis.model.Transactions
import com.yash.expense_analysis.utils.AppUtils
import com.yash.expense_analysis.utils.CacheUtils
import com.yash.expense_analysis.utils.Constants.CREDIT
import com.yash.expense_analysis.utils.Constants.DEBIT
import com.yash.expense_analysis.utils.Constants.EDIT_TAG
import com.yash.expense_analysis.utils.Constants.EMPTY_LIST
import com.yash.expense_analysis.utils.Constants.GRAPH_DATA
import com.yash.expense_analysis.utils.Constants.GRAPH_ERROR
import com.yash.expense_analysis.utils.Constants.GRAPH_LIST
import com.yash.expense_analysis.utils.Constants.HOME_SCREEN
import com.yash.expense_analysis.utils.Progress
import com.yash.expense_analysis.utils.SearchViewUtil
import kotlinx.android.synthetic.main.activity_main.*
import java.io.Serializable

class MainActivity : AppCompatActivity() {

    private val _searchTag = "SMS ANALYSIS :"
    private val REQUEST_CODE_ASK_PERMISSIONS = 123;
    private lateinit var analyseProgress: Progress
    private lateinit var expense: ArrayList<Transactions>
    private lateinit var income: ArrayList<Transactions>
    private var isLimitReached = false
    private lateinit var adapter: TransactionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.title = getString(R.string.lbl_home)
        analyseProgress = Progress(this, R.string.analysing)
        adapter = TransactionAdapter { performActions(it) }

        expense = arrayListOf()
        income = arrayListOf()

        showAlert(HOME_SCREEN)
        if (CacheUtils.getTransactions().isNullOrEmpty()) {
            analyseMessages()
        } else {
            processDataAndSetToAdapter()
        }
        hideKeyboard()
    }

    private fun hideKeyboard() = AppUtils.hideKeyboard(this)

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            android.R.id.home -> onBackPressed()

            R.id.action_graph -> {
                val graphArray = CacheUtils.getTransactions()
                val isTagFound = checkIfTagsExist(graphArray)

                if (graphArray.isNullOrEmpty()) {
                    showAlert(EMPTY_LIST)
                } else if (!isTagFound) {
                    showAlert(GRAPH_ERROR)
                } else {
                    val intent = Intent(this, GraphActivity::class.java)
                    val args = Bundle()
                    args.putSerializable(GRAPH_LIST, graphArray as Serializable)
                    intent.putExtra(GRAPH_DATA, args)
                    startActivity(intent)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_generic, menu)
        searchTags(menu)
        return true
    }

    private fun searchTags(menu: Menu?) {
        val findItem = menu!!.findItem(R.id.action_search)
        val searchView = findItem.actionView as SearchView
        searchView.setIconifiedByDefault(true)
        searchView.isIconified = true
        searchView.setOnCloseListener {
            val tempList = adapter.tempList
            adapter.mList = tempList
            adapter.notifyDataSetChanged()
            false
        }

        searchView.setOnQueryTextListener(object : SearchViewUtil() {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return false
            }
        })
    }

    private fun showAnalyseProgress() = analyseProgress.show()

    private fun stopAnalyseProgress() = analyseProgress.dismiss()

    @SuppressLint("Recycle")
    private fun analyseMessages() {

        showAnalyseProgress()

        if (ContextCompat.checkSelfPermission(
                baseContext,
                "android.permission.READ_SMS"
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            var cursor: Cursor? = null
            try {
                cursor =
                    contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null)

                if (cursor?.moveToFirst() == true) {
                    do {
                        var messageData = ""
                        var messageBody = ""
                        for (id in 0 until cursor.columnCount) {
                            messageData += " " + cursor.getColumnName(id)
                                .toString() + ":" + cursor.getString(id)

                            if (cursor.getColumnName(id)
                                    .equals(getString(R.string.body), true)
                            ) {
                                messageBody = cursor.getString(id)
                            }
                        }

                        if (messageData.contains(getString(R.string.account), true) &&
                            messageData.contains(getString(R.string.credit), true)
                        ) {
                            val transactions = Transactions()
                            transactions.messageBody = messageBody
                            transactions.transactionType = getString(R.string.credit)

                            val ePattern =
                                "[rR][sS]\\.?\\s[,\\d]+\\.?\\d{0,2}|[iI][nN][rR]\\.?\\s*[,\\d]+\\.?\\d{0,2}"
                            val p = java.util.regex.Pattern.compile(ePattern)
                            val m = p.matcher(messageBody)
                            if (m.find()) {
                                try {
                                    var amount: String = m.group(0) ?: "".replace("inr", "")
                                    amount = amount.replace("rs", "")
                                    amount = amount.replace("INR", "")
                                    amount = amount.replace("Rs", "")
                                    amount = amount.replace(" ", "")
                                    amount = amount.replace(",", "")

                                    transactions.totalAmount = amount
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }

                                transactions.id = income.size
                                if (transactions.totalAmount != null && income.size < 5) {
                                    income.add(transactions)
                                }
                            }

                        } else if (messageData.contains(getString(R.string.account), true) &&
                            messageData.contains(getString(R.string.debit), true)
                        ) {
                            val transactions = Transactions()
                            transactions.messageBody = messageBody
                            transactions.transactionType = getString(R.string.debit)

                            val ePattern =
                                "[rR][sS]\\.?\\s[,\\d]+\\.?\\d{0,2}|[iI][nN][rR]\\.?\\s*[,\\d]+\\.?\\d{0,2}"
                            val p = java.util.regex.Pattern.compile(ePattern)
                            val m = p.matcher(messageBody)
                            if (m.find()) {
                                try {
                                    var amount: String = m.group(0) ?: "".replace("inr", "")
                                    amount = amount.replace("rs", "")
                                    amount = amount.replace("INR", "")
                                    amount = amount.replace("Rs", "")
                                    amount = amount.replace(" ", "")
                                    amount = amount.replace(",", "")


                                    transactions.totalAmount = amount
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }

                                transactions.id = expense.size
                                if (transactions.totalAmount != null && expense.size < 5) {
                                    expense.add(transactions)
                                }
                            }
                        }

                        if (income.size > 4 && expense.size > 4) {
                            isLimitReached = true
                        }
                    } while (cursor.moveToNext() && !isLimitReached)
                } else {
                    Log.i(_searchTag, getString(R.string.no_sms_found))
                }
            } finally {
                stopAnalyseProgress()
                cursor?.close()
                CacheUtils.setExpense(expense)
                CacheUtils.setIncome(income)
                processDataAndSetToAdapter()
            }
        } else {
            getPermission()
        }
    }

    private fun getPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf("android.permission.READ_SMS"),
            REQUEST_CODE_ASK_PERMISSIONS
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            stopAnalyseProgress()
            showNoData()
        } else {
            analyseMessages()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun processDataAndSetToAdapter() {
        hideKeyboard()
        val expenseList = CacheUtils.getExpense()
        val incomeList = CacheUtils.getIncome()
        val totalTransactions = CacheUtils.getTransactions()
        if (expenseList.isNullOrEmpty() &&
            incomeList.isNullOrEmpty() &&
            totalTransactions.isNullOrEmpty()
        ) {
            showNoData()
        } else {
            val arrayList = ArrayList<Transactions>()
            if (!incomeList.isNullOrEmpty()) {
                arrayList.addAll(incomeList)
            }

            if (!expenseList.isNullOrEmpty()) {
                arrayList.addAll(expenseList)
            }

            if (!arrayList.isNullOrEmpty()) {
                CacheUtils.setTransactions(arrayList)
            } else {
                if (!totalTransactions.isNullOrEmpty()) {
                    arrayList.addAll(totalTransactions)
                } else {
                    showNoData()
                    return
                }
            }

            rcvTransactionList.visibility = View.VISIBLE
            tvNoData.visibility = View.GONE
            rcvTransactionList.layoutManager = LinearLayoutManager(this)
            rcvTransactionList.adapter = adapter
            adapter.mList = arrayList
            adapter.tempList = arrayList
            adapter.notifyDataSetChanged()
        }
    }

    private fun showNoData() {
        rcvTransactionList.visibility = View.GONE
        tvNoData.visibility = View.VISIBLE
    }

    private fun showAlert(identifier: String) {
        val builder = AlertDialog.Builder(this)

        when (identifier) {

            HOME_SCREEN -> {
                builder.setTitle(R.string.info)
                builder.setMessage(R.string.home_screen_info)
            }

            GRAPH_ERROR -> {
                builder.setIcon(android.R.drawable.ic_dialog_alert)
                builder.setTitle(R.string.no_tag_set)
                builder.setMessage(R.string.no_tag_error)
            }

            EMPTY_LIST -> {
                builder.setTitle(R.string.empty_list)
                builder.setMessage(R.string.empty_list_message)
            }
        }

        builder.setPositiveButton(getString(R.string.ok)) { _, _ -> }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.setCanceledOnTouchOutside(false)


        alertDialog.show()

        val icon: ImageView? = alertDialog.findViewById(android.R.id.icon)
        icon?.setColorFilter(
            Color.RED,
            PorterDuff.Mode.SRC_IN
        )
    }

    private fun performActions(pair: Pair<Transactions, String>) {
        val transaction = pair.first

        when (pair.second) {

            EDIT_TAG -> {
                val arrayList: List<Transactions>
                if (transaction.transactionType.equals(CREDIT, true)) {
                    arrayList = CacheUtils.getIncome() ?: arrayListOf()
                    arrayList[transaction.id!!].tag = transaction.tag
                    CacheUtils.setIncome(arrayList)
                    hideKeyboard()
                    processDataAndSetToAdapter()
                } else if (transaction.transactionType.equals(DEBIT, true)) {
                    arrayList = CacheUtils.getExpense() ?: arrayListOf()
                    arrayList[transaction.id!!].tag = transaction.tag
                    CacheUtils.setExpense(arrayList)
                    hideKeyboard()
                    processDataAndSetToAdapter()
                }
            }
        }
    }

    private fun checkIfTagsExist(arrayList: List<Transactions>?): Boolean {
        if (!arrayList.isNullOrEmpty()) {
            for (item in arrayList) {
                if (!item.tag.isNullOrEmpty()) {
                    return true
                }
            }
        }
        return false
    }
}