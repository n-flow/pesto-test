package com.parth.pestotest.ui.addTask.view

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.parth.pestotest.R
import com.parth.pestotest.core.broadcast.ReminderBroadcastReceiver
import com.parth.pestotest.databinding.ActivityAddTaskBinding
import com.parth.pestotest.network.model.TaskModel
import com.parth.pestotest.network.model.TaskStatusModel
import com.parth.pestotest.network.model.UserModel
import com.parth.pestotest.network.model.otherStatus
import com.parth.pestotest.network.model.unassigned
import com.parth.pestotest.ui.BaseActivity
import com.parth.pestotest.ui.addStaus.view.AddTaskStatusActivity
import com.parth.pestotest.ui.addTask.view.adapter.TaskStatusArrayAdapter
import com.parth.pestotest.ui.addTask.view.adapter.UsersArrayAdapter
import com.parth.pestotest.ui.addTask.viewmodel.AddTaskViewModel
import com.parth.pestotest.utils.DateFormats
import com.parth.pestotest.utils.dateToString
import com.parth.pestotest.utils.extensions.serializable
import com.parth.pestotest.utils.extensions.showToast
import com.parth.pestotest.utils.extensions.stringRes
import com.parth.pestotest.utils.hideKeyboard
import com.parth.pestotest.utils.openDatePickerDialog
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import java.util.Date


@AndroidEntryPoint
class AddTaskActivity : BaseActivity<ActivityAddTaskBinding>(R.layout.activity_add_task),
    View.OnClickListener {

    private val tasksList = ArrayList<TaskModel>()
    private val taskStatusList = ArrayList<TaskStatusModel>()
    private val usersList = ArrayList<UserModel>()

    val viewModel: AddTaskViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.onClick = this

        checkIntent()
        setObserve()
        updateDueDate(Date().time)
    }

    private fun checkIntent() {
        if (intent.hasExtra("editableTask")) {
            intent.serializable<TaskModel>("editableTask")?.let {
                viewModel.selectedTask = it
                binding.etTitle.setText(it.title)
                binding.etDescription.setText(it.description)
                binding.etDropdownTaskStatus.setText(it.status?.status ?: "")
                binding.etDueDates.setText(dateToString(it.dueDate, DateFormats.FORMAT_30))
                binding.etAssign.setText(it.user.personGivenName)
                binding.btnSubmit.text = stringRes(R.string.update)
            }
        }
    }

    private fun setObserve() {
        viewModel.tasksList.observe(this) {
            it?.let {
                tasksList.clear()
                tasksList.addAll(it)
            }
        }

        viewModel.taskStatusList.observe(this) {
            it?.let {
                taskStatusList.clear()
                taskStatusList.addAll(it)
                taskStatusList.add(otherStatus)
                initTaskStatusAdapter(taskStatusList)
            }
        }

        viewModel.usersList.observe(this) {
            it?.let {
                usersList.clear()
                usersList.addAll(it)
                usersList.add(0, unassigned)
                initUsersAdapter(usersList)
            }
        }
    }

    private fun initTaskStatusAdapter(list: ArrayList<TaskStatusModel>) {
        val taskStatusAdapter = TaskStatusArrayAdapter(this) {
            if (it.id == otherStatus.id) {
                viewModel.selectedTask.status = null
                startActivity(Intent(activity, AddTaskStatusActivity::class.java))
            } else {
                viewModel.selectedTask.status = it
            }
            binding.etDropdownTaskStatus.setText(viewModel.selectedTask.status?.status ?: "")
            hideKeyboard(activity)
            binding.etDropdownTaskStatus.clearFocus()
        }
        binding.etDropdownTaskStatus.setAdapter(taskStatusAdapter)
        taskStatusAdapter.updateValues(list)
        binding.etDropdownTaskStatus.setText(viewModel.selectedTask.status?.status ?: "")
    }

    private fun initUsersAdapter(list: ArrayList<UserModel>) {
        val userAdapter = UsersArrayAdapter(this) {
            viewModel.selectedTask.user = it
            binding.etAssign.setText(viewModel.selectedTask.user.personGivenName)
            hideKeyboard(activity)
            binding.etAssign.clearFocus()
            visibleAssignToMeLabel()
        }
        binding.etAssign.setAdapter(userAdapter)
        userAdapter.updateValues(list)
        binding.etAssign.setText(viewModel.selectedTask.user.personGivenName)
        visibleAssignToMeLabel()
    }

    private fun visibleAssignToMeLabel() {
        currentUser?.let {
            binding.txtAssignToMe.isVisible = viewModel.selectedTask.user.id != it.id
        }
    }

    private fun updateDueDate(date: Long) {
        viewModel.selectedTask.dueDate = date
        binding.etDueDates.setText(dateToString(date, DateFormats.FORMAT_30))
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.ivBack -> {
                finish()
            }

            binding.txtAssignToMe -> {
                viewModel.selectedTask.user = currentUser ?: unassigned
                binding.etAssign.setText(viewModel.selectedTask.user.personGivenName)
            }

            binding.boxDueDates, binding.etDueDates -> {
                openDatePickerDialog {
                    updateDueDate(it)
                }
            }

            binding.btnSubmit -> {
                val title = binding.etTitle.text?.toString()
                viewModel.selectedTask.title = title

                if (viewModel.selectedTask.title.isNullOrEmpty()) {
                    "Please enter task title".showToast(this)
                } else if (viewModel.selectedTask.status == null) {
                    "Please select task status".showToast(this)
                } else if (viewModel.selectedTask.dueDate == 0L) {
                    "Please select task due date".showToast(this)
                } else {
                    isShowPg()
                    viewModel.selectedTask.description = binding.etDescription.text?.toString()
                    setReminder(viewModel.selectedTask.dueDate)
                    viewModel.addTask {
                        finish()
                    }
                }
            }
        }
    }


    fun setReminder(tile: Long) {
        val calendar = Calendar.getInstance()
        calendar.time = Date(tile)
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 9, 0, 0)

        if (calendar.time.before(Date())) {
            calendar.time = Date()
            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE) + 1, calendar.get(Calendar.SECOND))
        }

        val intent = Intent(activity, ReminderBroadcastReceiver::class.java)
        intent.putExtra("task_name", "Complete Assignment")
        val pendingIntent = PendingIntent.getBroadcast(activity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent)
        "Reminder set successfully".showToast(this)
    }
}