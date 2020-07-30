package com.yash.expense_analysis.utils

import com.yash.expense_analysis.model.Transactions

/**
 * Created by Joshi on 26-07-2020.
 */
object CacheUtils {

    private lateinit var allTransactions: List<Transactions>
    private lateinit var income: List<Transactions>
    private lateinit var expense: List<Transactions>

    fun setTransactions(it: List<Transactions>?) {
        allTransactions = it!!
    }

    fun getTransactions(): List<Transactions>? {
        if (::allTransactions.isInitialized) {
            return allTransactions
        }
        return null
    }

    fun setIncome(it: List<Transactions>?) {
        income = it!!
    }

    fun getIncome(): List<Transactions>? {
        if (::income.isInitialized) {
            return income
        }
        return null
    }

    fun setExpense(it: List<Transactions>?) {
        expense = it!!
    }

    fun getExpense(): List<Transactions>? {
        if (::expense.isInitialized) {
            return expense
        }
        return null
    }
}