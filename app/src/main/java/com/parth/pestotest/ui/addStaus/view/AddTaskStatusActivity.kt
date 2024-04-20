package com.parth.pestotest.ui.addStaus.view

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.parth.pestotest.R
import com.parth.pestotest.databinding.ActivityAddTaskStatusBinding
import com.parth.pestotest.dialogs.common.CommonMsgDialog
import com.parth.pestotest.network.model.TaskStatusModel
import com.parth.pestotest.ui.BaseActivity
import com.parth.pestotest.ui.addStaus.view.adapter.StatusAdapter
import com.parth.pestotest.ui.addStaus.viewmodel.AddTaskStatusViewModel
import com.parth.pestotest.utils.extensions.showToast
import com.parth.pestotest.utils.extensions.stringRes
import com.parth.pestotest.utils.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint
import java.util.Date

@AndroidEntryPoint
class AddTaskStatusActivity :
    BaseActivity<ActivityAddTaskStatusBinding>(R.layout.activity_add_task_status),
    View.OnClickListener {

    private val viewModel: AddTaskStatusViewModel by viewModels()

    private var selectedTaskStatus = TaskStatusModel(Date().time.toString())

    private val taskStatusList = ArrayList<TaskStatusModel>()

    private val statusAdapter = StatusAdapter(::updateStatus, ::deleteStatus)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.onClick = this

        initView()
        setObserve()
    }

    private fun initView() {
        binding.rvStatus.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = statusAdapter
        }
    }

    private fun setObserve() {
        viewModel.taskStatusList.observe(this) {
            it?.let {
                taskStatusList.clear()
                taskStatusList.addAll(it)
                updateStatusList()
            }
        }
    }

    private fun updateStatusList() {
        val recyclerViewState = binding.rvStatus.layoutManager?.onSaveInstanceState()
        statusAdapter.updateList(taskStatusList)
        binding.rvStatus.layoutManager?.onRestoreInstanceState(recyclerViewState)
    }

    private fun deleteStatus(taskStatus: TaskStatusModel) {
        CommonMsgDialog(activity, "Delete Task Status", "Are you sure you want to delete this task status?") {
            if (it == CommonMsgDialog.POSITIVE_BTN_CLICK) {
                isShowPg()
                if (selectedTaskStatus.id == taskStatus.id) {
                    selectedTaskStatus = TaskStatusModel(Date().time.toString())
                }
                viewModel.removeTaskStatus(taskStatus)
            }
        }.openDialog()
    }

    private fun updateStatus(taskStatus: TaskStatusModel) {
        selectedTaskStatus = taskStatus
        binding.etTitle.setText(taskStatus.status ?: "Title Not Found")
        binding.etTitle.setSelection(binding.etTitle.text.toString().length)
        binding.btnSubmit.text = stringRes(R.string.update)
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.ivBack -> {
                finish()
            }

            binding.btnSubmit -> {
                if (binding.etTitle.text?.toString().isNullOrEmpty()) {
                    "Please enter task title".showToast(this)
                    return
                }
                selectedTaskStatus.status = binding.etTitle.text?.toString()
                viewModel.addTaskStatus(selectedTaskStatus)
                selectedTaskStatus = TaskStatusModel(Date().time.toString())
                binding.etTitle.setText("")
                binding.btnSubmit.text = stringRes(R.string.submit)

                hideKeyboard(activity)
                binding.etTitle.clearFocus()
            }
        }
    }
}