package com.yash.expense_analysis.model

import java.io.Serializable

/**
 * Created by Joshi on 26-07-2020.
 */
class Transactions : Serializable {
    var tag: String? = null
    var id: Int? = null
    var messageBody: String? = null
    var totalAmount: String? = null
    var transactionType: String? = null
}