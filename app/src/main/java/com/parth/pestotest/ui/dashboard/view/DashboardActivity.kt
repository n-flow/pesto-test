package com.parth.pestotest.ui.dashboard.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.parth.pestotest.R
import com.parth.pestotest.databinding.ActivityDashboardBinding
import com.parth.pestotest.dialogs.common.CommonMsgDialog
import com.parth.pestotest.network.model.TaskModel
import com.parth.pestotest.ui.BaseActivity
import com.parth.pestotest.ui.addTask.view.AddTaskActivity
import com.parth.pestotest.ui.dashboard.view.adapter.TasksAdapter
import com.parth.pestotest.ui.dashboard.viewmodel.DashboardViewModel
import com.parth.pestotest.ui.filter.data.model.FilterModel
import com.parth.pestotest.ui.filter.view.FilterActivity
import com.parth.pestotest.utils.extensions.parcelableArrayList
import com.parth.pestotest.utils.hideKeyboard
import com.parth.pestotest.utils.showKeyboard
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardActivity : BaseActivity<ActivityDashboardBinding>(R.layout.activity_dashboard),
    View.OnClickListener {

    private val tasksAdapter = TasksAdapter(::editTask, ::deleteTask)

    private var mBottomSheetDialogSorting: BottomSheetDialog? = null

    private val viewModel: DashboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.onClick = this

        initView()
        setObserver()
        requestPermissionForNotification()
    }

    private fun initView() {
        binding.rvTasks.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = tasksAdapter
        }

        binding.etSearch.addTextChangedListener(afterTextChanged = {
            viewModel.searchText = it?.toString() ?: ""
            viewModel.searchTask()
            binding.ivClear.isVisible = !it?.toString().isNullOrEmpty()
        })

        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                createSearchTextFocus()
            }
            true
        }
    }

    private fun setObserver() {
        viewModel.users.observe(this) {
            viewModel.updateFilters()
        }

        viewModel.taskStatus.observe(this) {
            viewModel.updateFilters()
        }

        viewModel.tasksList.observe(this) {
            viewModel.filterTask()
        }

        viewModel.tasks.observe(this) {
            updateTasksList(it)
        }
    }

    private fun updateTasksList(list: ArrayList<TaskModel>? = null) {
        binding.txtEmpty.isVisible = list.isNullOrEmpty()
        val recyclerViewState = binding.rvTasks.layoutManager?.onSaveInstanceState()
        tasksAdapter.updateList(list ?: ArrayList())
        binding.rvTasks.layoutManager?.onRestoreInstanceState(recyclerViewState)
    }

    private fun editTask(task: TaskModel) {
        startActivity(Intent(activity, AddTaskActivity::class.java).putExtra("editableTask", task))
    }

    private fun deleteTask(task: TaskModel) {
        CommonMsgDialog(activity, "Delete Task", "Are you sure you want to delete this task?") {
            if (it == CommonMsgDialog.POSITIVE_BTN_CLICK) {
                isShowPg()
                viewModel.deleteTask(task, {
                    isShowPg(false)
                }) {
                    isShowPg(false)
                }
            }
        }.openDialog()
    }

    private fun openSortDialog() {
        if (mBottomSheetDialogSorting == null) {
            mBottomSheetDialogSorting =
                viewModel.sortClass.createSortDialog(activity, viewModel.sortList) {
                    onSortClick(it)
                }
        }
        mBottomSheetDialogSorting?.show()
    }

    private fun onSortClick(i: Int) {
        createSearchTextFocus()
        viewModel.sortIndex = i
        viewModel.sortList.forEach {
            it.isSelect = false
        }
        viewModel.sortList[i].isSelect = true
        viewModel.sortClass.updateList(viewModel.sortList)
        mBottomSheetDialogSorting?.hide()
        viewModel.sortTask()
    }

    private fun createSearchTextFocus() {
        hideKeyboard(activity)
        binding.etSearch.clearFocus()
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.fabAddTask -> {
                createSearchTextFocus()
                startActivity(Intent(this, AddTaskActivity::class.java))
            }

            binding.ivSearch -> {
                showKeyboard(binding.etSearch, activity)
            }

            binding.ivClear -> {
                binding.etSearch.setText("")
                showKeyboard(binding.etSearch, activity)
            }

            binding.ivSort -> {
                createSearchTextFocus()
                openSortDialog()
            }

            binding.ivFilter -> {
                createSearchTextFocus()
                startActivityForResult(
                    Intent(
                        this,
                        FilterActivity::class.java
                    ).putParcelableArrayListExtra("FilterValue", viewModel.getFilter())
                ) {
                    it.data?.let { intent ->
                        if (intent.hasExtra("FilterValue")) {
                            intent.parcelableArrayList<FilterModel>("FilterValue")
                                ?.let { filteredValue ->
                                    viewModel.updateFilter(filteredValue)
                                }
                        }
                    }
                    createSearchTextFocus()
                }
            }
        }
    }

    private fun requestPermissionForNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            activity.requestPermissionLauncher(arrayOf(Manifest.permission.POST_NOTIFICATIONS)) {
            }
        }
    }
}