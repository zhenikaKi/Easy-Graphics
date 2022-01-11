package ru.easygraphics.tableWindow.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.ListAdapter
import ru.easygraphics.R
import ru.easygraphics.data.domain.TableLineData
import ru.easygraphics.helpers.consts.App

class TableAdapter(
    private val delegate: Delegate
) : ListAdapter<TableLineData, TableViewHolder>(TableDiffUtil) {

    interface Delegate {
        fun onRowSelected(tableLineData: TableLineData)
        fun onDeleteSelected(position: Int)
    }

    private lateinit var context: Context
    private val removedItems = arrayListOf<TableLineData>()

    private val differ = AsyncListDiffer(this, TableDiffUtil)

    fun setData(tableData: List<TableLineData>){
        differ.submitList(tableData)
    }

    fun deleteElement(position: Int) {
        val item = differ.currentList[position]
        val list = differ.currentList
        list.remove(item)
        differ.submitList(list)

        /*currentList.removeAt(position)
        submit(currentList, true)
        notifyDataSetChanged()*/
    }

    /*fun deleteElement(position: Int) {
        Log.d(App.LOG_TAG, "$position")
        //if (position == -1) return null
        return removeItem(position)
    }*/

    /*private fun submit(list: List<TableLineData>?, isLocalSubmit: Boolean) {
        if (!isLocalSubmit) removedItems.clear()
        super.submitList(list)
    }

    override fun submitList(list: List<TableLineData>?) {
        submit(list, false)
    }*/

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TableViewHolder {
        context = parent.context

        val rootView = LayoutInflater.from(context)
            .inflate(R.layout.table_line, parent, false)

        return TableViewHolder(rootView)
    }

    override fun onBindViewHolder(holder: TableViewHolder, position: Int) {
        holder.bind(differ.currentList[position], context = context, delegate)
}

    override fun getItemId(position: Int) = position.toLong()

    override fun getItemViewType(position: Int) = position

    override fun getItemCount(): Int = differ.currentList.size
}