package com.parth.pestotest.ui.addStaus.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.parth.pestotest.network.firebase.FirebaseDataListen
import com.parth.pestotest.network.model.TaskStatusModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddTaskStatusViewModel @Inject constructor(
    private val firebaseDataListen: FirebaseDataListen
) : ViewModel() {

    val taskStatusList: LiveData<ArrayList<TaskStatusModel>>
        get() = firebaseDataListen.taskStatus

    fun addTaskStatus(taskStatus: TaskStatusModel) {
        firebaseDataListen.addTaskStatus(taskStatus)
    }

    fun removeTaskStatus(taskStatus: TaskStatusModel) {
        firebaseDataListen.removeTaskStatus(taskStatus)
    }
}