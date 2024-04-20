package com.parth.pestotest.network.firebase

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import com.parth.pestotest.network.model.TaskModel
import com.parth.pestotest.network.model.TaskStatusModel
import com.parth.pestotest.network.model.UserModel
import com.parth.pestotest.utils.extensions.deleteIf
import com.parth.pestotest.utils.extensions.email
import com.parth.pestotest.utils.logger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseDataListen @Inject constructor(
    private val preferences: SharedPreferences
) {

    private var _user = MutableLiveData<UserModel>()
    val user: LiveData<UserModel>
        get() = _user

    private var _users = MutableLiveData<ArrayList<UserModel>>()
    val users: LiveData<ArrayList<UserModel>>
        get() = _users

    private var _tasks = MutableLiveData<ArrayList<TaskModel>>()
    val tasks: LiveData<ArrayList<TaskModel>>
        get() = _tasks

    private var _taskStatus = MutableLiveData<ArrayList<TaskStatusModel>>()
    val taskStatus: LiveData<ArrayList<TaskStatusModel>>
        get() = _taskStatus

    private val database = Firebase.database.reference

    init {
        observeUsers()
        observeTasks()
        observeTaskStatus()
    }

    fun registerUser(userModel: UserModel) {
        database.child(TABLE_USER).child(userModel.id).setValue(userModel)
            .addOnCompleteListener {
                preferences.email = userModel.id
                _user.postValue(userModel)
            }
            .addOnFailureListener {
                it.printStackTrace()
            }
    }

    fun signInUser() {
        database.child(TABLE_USER).child(preferences.email).get()
            .addOnSuccessListener {
                if (it.exists() && it != null) {
                    _user.postValue(it.getValue<UserModel>())
                    preferences.email = _user.value?.id ?: preferences.email
                }
            }
            .addOnFailureListener {
                it.printStackTrace()
            }
    }

    private fun observeUsers() {
        val tasks = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val users = ArrayList<UserModel>()
                for (task in dataSnapshot.children) {
                    task.getValue<UserModel>()?.let {
                        users.add(it)
                    }
                }

                _users.postValue(users)
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }
        database.child(TABLE_USER).addValueEventListener(tasks)
    }

    fun addTask(task: TaskModel, onSuccess: OnSuccessListener<Void>) {
        database.child(TABLE_TASK).child(task.id).setValue(task).addOnSuccessListener(onSuccess)
    }

    fun removeTask(task: TaskModel, onSuccess: OnSuccessListener<Void>, fail: OnFailureListener) {
        database.child(TABLE_TASK).child(task.id).removeValue()
            .addOnSuccessListener {
                onSuccess.onSuccess(it)
                val tasks = ArrayList<TaskModel>(_tasks.value ?: emptyList())
                tasks.deleteIf { it.id == task.id }
                _tasks.postValue(tasks)
            }.addOnFailureListener {
                it.printStackTrace()
                fail.onFailure(it)
            }.addOnCanceledListener {
                logger("addOnCanceledListener", "addOnCanceledListener")
            }
    }

    private fun observeTasks() {
        val tasks = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val tasksList = ArrayList<TaskModel>()
                for (task in dataSnapshot.children) {
                    task.getValue<TaskModel>()?.let {
                        tasksList.add(it)
                    }
                }

                _tasks.postValue(tasksList)
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }
        database.child(TABLE_TASK).addValueEventListener(tasks)
    }

    fun addTaskStatus(taskStatus: TaskStatusModel) {
        val taskStatusList = ArrayList<TaskStatusModel>(_taskStatus.value ?: emptyList())
        if (taskStatusList.any { it.id == taskStatus.id }) {
            taskStatusList[taskStatusList.indexOfFirst { it.id == taskStatus.id }] = taskStatus
        } else {
            taskStatusList.add(taskStatus)
        }
        _taskStatus.postValue(taskStatusList)
        database.child(TABLE_TASK_STATUS).child(taskStatus.id).setValue(taskStatus)
    }

    fun removeTaskStatus(taskStatus: TaskStatusModel) {
        val taskStatusList = ArrayList<TaskStatusModel>(_taskStatus.value ?: emptyList())
        taskStatusList.deleteIf { it.id == taskStatus.id }
        _taskStatus.postValue(taskStatusList)
        database.child(TABLE_TASK_STATUS).child(taskStatus.id).removeValue()
    }

    private fun observeTaskStatus() {
        val tasks = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val taskStatusList = ArrayList<TaskStatusModel>()
                for (task in dataSnapshot.children) {
                    task.getValue<TaskStatusModel>()?.let {
                        taskStatusList.add(it)
                    }
                }

                _taskStatus.postValue(taskStatusList)
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }
        database.child(TABLE_TASK_STATUS).addValueEventListener(tasks)
    }
}