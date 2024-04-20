package com.parth.pestotest.ui.addTask.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.parth.pestotest.network.firebase.FirebaseDataListen
import com.parth.pestotest.network.model.TaskModel
import com.parth.pestotest.network.model.TaskStatusModel
import com.parth.pestotest.network.model.UserModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AddTaskViewModel @Inject constructor(
    val firebaseDataListen: FirebaseDataListen
) : ViewModel() {

    var selectedTask = TaskModel(Date().time.toString())

    val tasksList: LiveData<ArrayList<TaskModel>>
        get() = firebaseDataListen.tasks

    val taskStatusList: LiveData<ArrayList<TaskStatusModel>>
        get() = firebaseDataListen.taskStatus

    val usersList: LiveData<ArrayList<UserModel>>
        get() = firebaseDataListen.users

    fun addTask(success: () -> Unit = {}) {
        if (selectedTask.createdAt == 0L) {
            selectedTask.createdAt = Date().time
        }
        selectedTask.updatedAt = Date().time
        firebaseDataListen.addTask(selectedTask) {
            success.invoke()
        }
    }

    fun getFirstTaskStatus() = firebaseDataListen.taskStatus.value?.lastOrNull()
}