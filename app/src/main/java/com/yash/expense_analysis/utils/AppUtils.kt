package com.yash.expense_analysis.utils

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager

/**
 * Created by Joshi on 26-07-2020.
 */
object AppUtils {

    fun hideKeyboard(activity: Activity) {
        val view = activity.currentFocus
        if (view != null) {
            val inputMethodManager =
                activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            inputMethodManager!!.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}