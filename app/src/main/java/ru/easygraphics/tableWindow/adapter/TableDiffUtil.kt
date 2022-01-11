package ru.easygraphics.tableWindow.adapter

import androidx.recyclerview.widget.DiffUtil
import ru.easygraphics.data.domain.TableLineData

object TableDiffUtil : DiffUtil.ItemCallback<TableLineData>() {

    private val payload = Any()
    override fun areItemsTheSame(oldItem: TableLineData, newItem: TableLineData): Boolean {
        return oldItem.lineId == newItem.lineId
    }

    override fun areContentsTheSame(oldItem: TableLineData, newItem: TableLineData): Boolean {
        return oldItem == newItem
    }

    override fun getChangePayload(oldItem: TableLineData, newItem: TableLineData) = payload
}