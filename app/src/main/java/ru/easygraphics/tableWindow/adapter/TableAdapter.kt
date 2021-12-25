package ru.easygraphics.tableWindow.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import ru.easygraphics.R
import ru.easygraphics.data.domain.TableLineData

class TableAdapter() : ListAdapter<TableLineData, TableViewHolder>(TableDiffUtil) {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TableViewHolder {
        val rootView = LayoutInflater.from(parent.context)
            .inflate(R.layout.table_line, parent, false)

        context = parent.context

        return TableViewHolder(rootView)
    }

    override fun onBindViewHolder(holder: TableViewHolder, position: Int) =
        holder.bind(getItem(position), context = context)

    override fun getItemId(position: Int) = position.toLong()

    override fun getItemViewType(position: Int) = position
}