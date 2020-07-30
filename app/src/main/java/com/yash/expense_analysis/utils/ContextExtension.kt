package com.yash.expense_analysis.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.LayoutRes

/**
 * Created by Joshi on 26-07-2020.
 */
fun Context.inflate(@LayoutRes layoutRes: Int): View? {
    return LayoutInflater.from(this).inflate(layoutRes, null)
}