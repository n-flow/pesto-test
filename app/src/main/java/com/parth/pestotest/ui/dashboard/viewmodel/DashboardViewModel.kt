package com.parth.pestotest.ui.dashboard.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.parth.pestotest.dialogs.sort.SortClass
import com.parth.pestotest.dialogs.sort.getSortModel
import com.parth.pestotest.network.firebase.FirebaseDataListen
import com.parth.pestotest.network.model.TaskModel
import com.parth.pestotest.network.model.TaskStatusModel
import com.parth.pestotest.network.model.UserModel
import com.parth.pestotest.network.model.unassigned
import com.parth.pestotest.ui.filter.data.model.FilterId
import com.parth.pestotest.ui.filter.data.model.FilterModel
import com.parth.pestotest.ui.filter.data.model.FilterValue
import com.parth.pestotest.ui.filter.data.util.getTaskFilters
import com.parth.pestotest.utils.extensions.deleteIf
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val firebaseDataListen: FirebaseDataListen
) : ViewModel() {

    private var filters = getTaskFilters()

    val sortClass = SortClass()
    val sortList = getSortModel()
    var searchText = ""
    var sortIndex = 0

    val tasksList: LiveData<ArrayList<TaskModel>>
        get() = firebaseDataListen.tasks

    val users: LiveData<ArrayList<UserModel>>
        get() = firebaseDataListen.users

    val taskStatus: LiveData<ArrayList<TaskStatusModel>>
        get() = firebaseDataListen.taskStatus

    private var filterTasks = ArrayList<TaskModel>()
    private var searchTasks = ArrayList<TaskModel>()

    private var _tasks = MutableLiveData<ArrayList<TaskModel>>()
    val tasks: LiveData<ArrayList<TaskModel>>
        get() = _tasks

    private var _filter = MutableLiveData<ArrayList<FilterModel>>()
    val filter: LiveData<ArrayList<FilterModel>>
        get() = _filter

    fun deleteTask(task: TaskModel, onSuccess: OnSuccessListener<Void>, fail: OnFailureListener) {
        firebaseDataListen.removeTask(task, onSuccess, fail)
    }

    fun filterTask() {
        val list = ArrayList<TaskModel>()
        list.addAll(tasksList.value ?: ArrayList())

        filters.first { it.id == FilterId.STATUS }.apply {
            if (filterValue.any { it.isChecked }) {
                val idMap = filterValue.map { if (it.isChecked) it.id else "-2" }
                list.deleteIf { !idMap.contains(it.status?.id) }
            }
        }

        filters.first { it.id == FilterId.USERS }.apply {
            if (filterValue.any { it.isChecked }) {
                val idMap = filterValue.map { if (it.isChecked) it.id else "-2" }
                list.deleteIf { !idMap.contains(it.user.id) }
            }
        }

        filterTasks = list
        searchTask()
    }

    fun searchTask() {
        val list = ArrayList<TaskModel>()
        list.addAll(filterTasks)

        if (searchText.isNotEmpty()) {
            list.deleteIf {
                it.status?.status?.contains(searchText, true) != true &&
                        !it.user.personGivenName.contains(searchText, true) &&
                        !it.user.personFamilyName.contains(searchText, true) &&
                        it.title?.contains(searchText, true) != true
            }
        }

        searchTasks = list
        sortTask()
    }

    fun sortTask() {
        val list = ArrayList<TaskModel>()
        list.addAll(searchTasks)

        when (sortIndex) {
            1 -> {
                list.sortWith { o1, o2 -> o2.createdAt.compareTo(o1.createdAt) }
            }

            2 -> {
                list.sortWith { o1, o2 -> o1.dueDate.compareTo(o2.dueDate) }
            }

            3 -> {
                list.sortWith { o1, o2 -> o2.dueDate.compareTo(o1.dueDate) }
            }

            4 -> {
                list.sortWith { o1, o2 -> o1.title?.compareTo(o2.title ?: "") ?: 0 }
            }

            5 -> {
                list.sortWith { o1, o2 -> o2.title?.compareTo(o1.title ?: "") ?: 0 }
            }

            else -> {
                list.sortWith { o1, o2 -> o1.createdAt.compareTo(o2.createdAt) }
            }
        }
        _tasks.postValue(list)
    }

    fun updateFilter(filter: ArrayList<FilterModel>) {
        filters = filter
        filterTask()
    }

    fun getFilter() = filters

    fun updateFilters() {
        val statusValues = ArrayList<FilterValue>()
        taskStatus.value?.forEach {
            statusValues.add(FilterValue(it.id, it.status ?: ""))
        }
        filters.first { it.id == FilterId.STATUS }.apply {
            if (filters.isEmpty()) {
                filterValue = statusValues
            } else {
                statusValues.deleteIf { status -> filterValue.any { it.id == status.id } }
                filterValue.addAll(statusValues)
            }
        }

        val userList = ArrayList<FilterValue>()
        users.value?.forEach {
            userList.add(FilterValue(it.id, it.personName))
        }
        userList.add(0, FilterValue(unassigned.id, unassigned.personName))
        filters.first { it.id == FilterId.USERS }.apply {
            if (filters.isEmpty()) {
                filterValue = userList
            } else {
                userList.deleteIf { status -> filterValue.any { it.id == status.id } }
                filterValue.addAll(userList)
            }
        }
    }
}