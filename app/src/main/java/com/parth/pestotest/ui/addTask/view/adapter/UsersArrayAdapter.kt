package com.parth.pestotest.ui.addTask.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import com.parth.pestotest.R
import com.parth.pestotest.databinding.DropDownItemsBinding
import com.parth.pestotest.network.model.UserModel

class UsersArrayAdapter(
    context: Context,
    private val onUserClick: (UserModel) -> Unit = {}
) : ArrayAdapter<UserModel>(context, R.layout.drop_down_items) {

    private var originalList = ArrayList<UserModel>()
    private var filteredList = ArrayList<UserModel>()

    private var mFilter: CustomFilter? = null

    fun updateValues(data: ArrayList<UserModel>, isClearFilterValue: Boolean = true) {
        originalList.clear()
        originalList.addAll(data)
        if (isClearFilterValue) {
            filteredList.clear()
            filteredList.addAll(data)
        }
        if (filteredList.size > 0) {
            notifyDataSetChanged()
        } else {
            notifyDataSetInvalidated()
        }
    }

    override fun getFilter(): Filter {
        if (mFilter == null)
            mFilter = CustomFilter()
        return mFilter!!
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val binding: DropDownItemsBinding
        if (view == null) {
            binding =
                DropDownItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            view = binding.root
        } else {
            binding = view.tag as DropDownItemsBinding
        }

        binding.txtStatus.text = filteredList[position].personGivenName
        view.tag = binding

        binding.root.setOnClickListener {
            onUserClick.invoke(filteredList[position])
        }

        return view
    }

    override fun getCount() = filteredList.size

    override fun getItem(position: Int): UserModel {
        return filteredList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    private inner class CustomFilter : Filter() {
        override fun performFiltering(prefix: CharSequence?): FilterResults {
            val filteredItems = ArrayList<UserModel>()
            val filters = originalList.filter {
                it.personGivenName.lowercase().contains(prefix.toString().lowercase()) ||
                        it.personFamilyName.lowercase().contains(prefix.toString().lowercase()) ||
                        it.personName.lowercase().contains(prefix.toString().lowercase())
            }
            filteredItems.addAll(filters)

            val results = FilterResults()
            results.count = if (prefix.isNullOrEmpty()) originalList.size else filteredItems.size
            results.values = if (prefix.isNullOrEmpty()) originalList else filteredItems

            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults) {
            @Suppress("UNCHECKED_CAST")
            if (results.values != null && results.values is ArrayList<*>) {
                filteredList.clear()
                filteredList.addAll(results.values as Collection<UserModel>)
            }

            if (filteredList.size > 0) {
                notifyDataSetChanged()
            } else {
                notifyDataSetInvalidated()
            }
        }
    }
}