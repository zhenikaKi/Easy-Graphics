package ru.easygraphics.tableWindow.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import ru.easygraphics.R
import ru.easygraphics.data.domain.LineDetails
import ru.easygraphics.data.domain.TableLineData
import ru.easygraphics.helpers.consts.App

class TableAdapter(
    private val delegate: Delegate?
) : ListAdapter<TableLineData, TableViewHolder>(TableDiffUtil) {

    interface Delegate {
        fun onRowSelected(tableLineData: TableLineData)
        fun onDeleteSelected(tableLineData: TableLineData)
    }

    private lateinit var context: Context
    private val removedItems = arrayListOf<TableLineData>()

    private fun removeItem(position: Int): TableLineData? {
        if (position >= itemCount) return null
        val item = currentList[position]
        Log.d(App.LOG_TAG, "$item")
        removedItems.add(item)
        val actualList = currentList - removedItems
        Log.d(App.LOG_TAG, "Actual size: " + actualList.size.toString())
        if (actualList.isEmpty()) removedItems.clear()
        submit(actualList, true)
        return item
    }

    fun removeItem(item: TableLineData): TableLineData? {
        val position = currentList.indexOf(item)
        Log.d(App.LOG_TAG, "$position")
        if (position == -1) return null
        return removeItem(position)
    }

    private fun submit(list: List<TableLineData>?, isLocalSubmit: Boolean) {
        if (!isLocalSubmit) removedItems.clear()
        super.submitList(list)
    }

    override fun submitList(list: List<TableLineData>?) {
        submit(list, false)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TableViewHolder {
        context = parent.context

        val rootView = LayoutInflater.from(context)
            .inflate(R.layout.table_line, parent, false)

        return TableViewHolder(rootView)
    }

    override fun onBindViewHolder(holder: TableViewHolder, position: Int) {
        //holder.bind(getItem(position), context = context, delegate)
}

    override fun getItemId(position: Int) = position.toLong()

    override fun getItemViewType(position: Int) = position
}