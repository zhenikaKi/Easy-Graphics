package ru.easygraphics.tableWindow.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import ru.easygraphics.R
import ru.easygraphics.data.domain.TableLineData

class TableAdapter(
    private val delegate: Delegate?
) : ListAdapter<TableLineData, TableViewHolder>(TableDiffUtil) {

    interface Delegate {
        fun onRowSelected(tableLineData: TableLineData)
    }

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TableViewHolder {
        context = parent.context

        val rootView = LayoutInflater.from(context)
            .inflate(R.layout.table_line, parent, false)

        return TableViewHolder(rootView)
    }

    override fun onBindViewHolder(holder: TableViewHolder, position: Int) =
        holder.bind(getItem(position), context = context, delegate)

    override fun getItemId(position: Int) = position.toLong()

    override fun getItemViewType(position: Int) = position
}