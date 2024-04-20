package com.parth.pestotest.ui.filter.data.util

import com.parth.pestotest.ui.filter.data.model.FilterId
import com.parth.pestotest.ui.filter.data.model.FilterModel
import com.parth.pestotest.ui.filter.data.model.FilterType

fun getTaskFilters(): ArrayList<FilterModel> {
    val data = ArrayList<FilterModel>()

    data.add(getStatus())
    data.add(getUsersValue())

    return data
}


fun getStatus(): FilterModel {
    return FilterModel(
        id = FilterId.STATUS,
        type = FilterType.CHECK_BOX,
        name = "Task Status",
        filterValue = ArrayList(),
        isChecked = false
    )
}

fun getUsersValue(): FilterModel {
    return FilterModel(
        id = FilterId.USERS,
        type = FilterType.CHECK_BOX,
        name = "Users",
        filterValue = ArrayList(),
        isChecked = false
    )
}