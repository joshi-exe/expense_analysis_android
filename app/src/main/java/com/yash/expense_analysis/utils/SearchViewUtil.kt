package com.yash.expense_analysis.utils

import androidx.appcompat.widget.SearchView

/**
 * Created by Joshi on 26-07-2020.
 */
open class SearchViewUtil : SearchView.OnQueryTextListener {

    override fun onQueryTextSubmit(query: String?): Boolean = false

    override fun onQueryTextChange(newText: String?): Boolean = false
}