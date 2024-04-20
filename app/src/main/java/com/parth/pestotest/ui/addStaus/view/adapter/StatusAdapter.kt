package com.parth.pestotest.ui.addStaus.view.adapter

import androidx.recyclerview.widget.DiffUtil
import com.parth.pestotest.R
import com.parth.pestotest.databinding.AdapterStatusBinding
import com.parth.pestotest.network.model.TaskStatusModel
import com.parth.pestotest.ui.BaseAdapter

class StatusAdapter(
    private val onUpdateClick: (TaskStatusModel) -> Unit = {},
    private val onDeleteClick: (TaskStatusModel) -> Unit = {}
) : BaseAdapter<AdapterStatusBinding>(R.layout.adapter_status) {

    val status = ArrayList<TaskStatusModel>()

    fun updateList(newItems: ArrayList<TaskStatusModel>) {
        val oldItems: ArrayList<TaskStatusModel> = ArrayList(status)
        status.clear()
        status.addAll(newItems)

        DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int {
                return oldItems.size
            }

            override fun getNewListSize(): Int {
                return status.size
            }

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldItems[oldItemPosition] == newItems[newItemPosition]
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldItems[oldItemPosition] == newItems[newItemPosition]
            }
        }).dispatchUpdatesTo(this)
    }

    override fun onBindViewHolder(holder: ViewHolder<AdapterStatusBinding>, position: Int) {

        holder.binding.txtTitle.text = status[position].status ?: "Status Not Found"

        holder.binding.ivDelete.setOnClickListener {
            onDeleteClick.invoke(status[position])
        }

        holder.binding.ivEdit.setOnClickListener {
            onUpdateClick.invoke(status[position])
        }
    }

    override fun getItemCount() = status.size
}