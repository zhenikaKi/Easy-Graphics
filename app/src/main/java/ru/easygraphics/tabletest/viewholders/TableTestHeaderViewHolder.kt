package ru.easygraphics.tabletest.viewholders

import android.view.LayoutInflater
import android.view.ViewGroup
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder
import ru.easygraphics.databinding.TableViewHeaderLayoutBinding
import ru.easygraphics.tabletest.data.Cell

class TableTestHeaderViewHolder(private val binding: TableViewHeaderLayoutBinding): AbstractViewHolder(binding.root) {
    companion object {
        fun create(parent: ViewGroup): TableTestHeaderViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = TableViewHeaderLayoutBinding.inflate(inflater, parent, false)
            return TableTestHeaderViewHolder(binding)
        }
    }

    /** Заполнить данные по заголоку */
    fun setCellData(cell: Cell?) {
        binding.columnHeaderText.text = cell?.value
    }
}