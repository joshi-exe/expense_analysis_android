package com.yash.expense_analysis

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.yash.expense_analysis.model.Transactions
import com.yash.expense_analysis.utils.Constants.CREDIT
import com.yash.expense_analysis.utils.Constants.DEBIT
import com.yash.expense_analysis.utils.Constants.GRAPH_DATA
import com.yash.expense_analysis.utils.Constants.GRAPH_LIST
import kotlinx.android.synthetic.main.activity_graph.*
import java.math.RoundingMode
import java.text.DecimalFormat


/**
 * Created by Joshi on 26-07-2020.
 */
class GraphActivity : AppCompatActivity() {

    private lateinit var transactionsList: List<Transactions>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val args = intent?.getBundleExtra(GRAPH_DATA)
        @Suppress("UNCHECKED_CAST") val arrayList =
            args?.getSerializable(GRAPH_LIST) as ArrayList<Transactions>?

        transactionsList = arrayList ?: arrayListOf()
        setGraphData(transactionsList)
        evaluateTransactions(transactionsList)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    private fun setGraphData(arrayList: List<Transactions>) {
        if (!arrayList.isNullOrEmpty()) {
            val label = arrayListOf<String?>()

            val credits = ArrayList<BarEntry>()
            val debits = ArrayList<BarEntry>()

            for (i in arrayList.indices) {
                if (arrayList[i].transactionType.equals(CREDIT, true)) {
                    credits.add(BarEntry(i + 0.0f, arrayList[i].totalAmount!!.toFloat()))
                } else if (arrayList[i].transactionType.equals(DEBIT, true)) {
                    debits.add(BarEntry(i + 0.0f, arrayList[i].totalAmount!!.toFloat()))
                }
                label.add(i, arrayList[i].tag ?: "n/a")
            }

            val income = BarDataSet(credits, getString(R.string.credit))
            income.color = getColor(R.color.green)

            val expense = BarDataSet(debits, getString(R.string.debit))
            expense.color = getColor(R.color.red)

            barChart.description.isEnabled = false

            val groupSpace = 0.2f
            val barSpace = 0.8f
            val barWidth = 0.4f
            val data = BarData(income, expense)
            data.barWidth = barWidth
            barChart.data = data
            barChart.setVisibleXRangeMaximum(5F)
            val axisRight = barChart.axisLeft
            axisRight.axisMinimum = 0f
            val xAxis = barChart.xAxis
            xAxis.labelCount = label.size
            xAxis.valueFormatter = IndexAxisValueFormatter(label)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.granularity = 1f
            xAxis.setDrawGridLines(false)
            xAxis.axisMinimum = -0.5f
            xAxis.axisMaximum = 5f + barChart.barData.getGroupWidth(groupSpace, barSpace) * 5
            barChart.xAxis.setCenterAxisLabels(false)
            xAxis.setCenterAxisLabels(false)
            xAxis.labelRotationAngle = 40f
            barChart.axisRight.isEnabled = false
            barChart.axisLeft.setDrawGridLines(false)
            barChart.invalidate()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun evaluateTransactions(arrayList: List<Transactions>) {
        var income = 0f
        var expense = 0f
        for (item in arrayList) {
            if (item.transactionType.equals(CREDIT, true)) {
                income += item.totalAmount!!.toFloat()
            } else {
                expense += item.totalAmount!!.toFloat()
            }
        }

        tvIncome.text = getString(R.string.rupee_symbol) + income
        tvExpense.text = getString(R.string.rupee_symbol) + expense
        val percentage = (expense / income) * 100
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.CEILING
        val round = df.format(percentage)
        tvPercentage.text = "$round%"
    }
}