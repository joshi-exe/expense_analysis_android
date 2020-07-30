package com.yash.expense_analysis.utils

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.annotation.StringRes
import com.yash.expense_analysis.R

/**
 * Created by Joshi on 26-07-2020.
 */
class Progress constructor(context: Context?, @StringRes private val titleRes: Int) {

    private var view: View? = null
    private var builder: AlertDialog.Builder
    private var dialog: Dialog

    init {
        view = context?.inflate(R.layout.progress)
        view?.findViewById<TextView>(R.id.text)?.setText(titleRes)
        builder = AlertDialog.Builder(context)
        builder.setView(view)
        dialog = builder.create()
    }

    fun show() {
        if (!dialog.isShowing) {
            dialog.setCancelable(false)
            dialog.setCanceledOnTouchOutside(false)
            dialog.show()
        }
    }

    fun dismiss() {
        if (dialog.isShowing) dialog.dismiss()
    }
}