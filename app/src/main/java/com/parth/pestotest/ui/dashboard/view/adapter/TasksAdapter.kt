package com.parth.pestotest.ui.dashboard.view.adapter

import android.graphics.Typeface
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import com.parth.pestotest.R
import com.parth.pestotest.databinding.AdapterTasksBinding
import com.parth.pestotest.network.model.TaskModel
import com.parth.pestotest.ui.BaseAdapter
import com.parth.pestotest.utils.DateFormats
import com.parth.pestotest.utils.dateToString
import com.parth.pestotest.utils.extensions.loadImage
import com.parth.pestotest.utils.extensions.makeTextDrawable
import java.util.Date

class TasksAdapter(
    private val onEditClick: (TaskModel) -> Unit = {},
    private val onDeleteClick: (TaskModel) -> Unit = {},
) : BaseAdapter<AdapterTasksBinding>(R.layout.adapter_tasks) {

    val tasks = ArrayList<TaskModel>()
    private val imageDrawableMap = mutableMapOf<String, Any?>()

    fun updateList(newItems: ArrayList<TaskModel>) {
        val oldItems: ArrayList<TaskModel> = ArrayList(tasks)
        tasks.clear()
        tasks.addAll(newItems)

        DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int {
                return oldItems.size
            }

            override fun getNewListSize(): Int {
                return tasks.size
            }

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldItems[oldItemPosition] == newItems[newItemPosition]
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldItems[oldItemPosition] == newItems[newItemPosition]
            }
        }).dispatchUpdatesTo(this)
    }

    override fun onBindViewHolder(holder: ViewHolder<AdapterTasksBinding>, position: Int) {
        val task = tasks[position]
        holder.binding.data = task
        var userImage = imageDrawableMap[task.user.id]
        if (task.user.personPhoto.isNullOrEmpty()) {
            if (userImage == null) {
                makeTextDrawable(holder.binding.root.context, task.user) {
                    userImage = it
                    imageDrawableMap[task.user.id] = userImage
                }
            }
        }

        if (userImage is Drawable) {
            holder.binding.ivImage.setImageDrawable(userImage as Drawable)
        } else {
            holder.binding.ivImage.loadImage(userImage)
        }

        val today = dateToString(Date().time, DateFormats.FORMAT_30)
        val taskDueDate = dateToString(task.dueDate, DateFormats.FORMAT_30)
        holder.binding.txtTaskDueDate.text = taskDueDate

        val dateColor = when {
            !task.status?.status.equals("done", true) && (today.equals(taskDueDate, true) || Date(task.dueDate).before(Date())) -> {
                R.color.red
            }
            else -> {
                R.color.black
            }
        }
        holder.binding.txtTaskDueDate.setTextColor(ContextCompat.getColor(holder.binding.root.context, dateColor))
        holder.binding.txtTaskDueDate.setTypeface(holder.binding.txtTaskDueDate.typeface, if (dateColor == R.color.red) Typeface.BOLD else Typeface.NORMAL)

        val statusColor = when {
            task.status?.status.equals("done", true) -> {
                R.color.green
            }
            else -> {
                R.color.black
            }
        }
        holder.binding.txtTaskStatus.setTextColor(ContextCompat.getColor(holder.binding.root.context, statusColor))

        holder.binding.ivEdit.setOnClickListener {
            onEditClick.invoke(task)
        }

        holder.binding.ivDelete.setOnClickListener {
            onDeleteClick.invoke(task)
        }
    }

    override fun getItemCount() = tasks.size
}