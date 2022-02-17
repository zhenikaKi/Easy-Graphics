package ru.easygraphics.tabletest.viewholders

import android.view.LayoutInflater
import android.view.ViewGroup
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder
import ru.easygraphics.R
import ru.easygraphics.databinding.TableViewCellLayoutBinding
import ru.easygraphics.tabletest.data.Cell
import ru.easygraphics.tabletest.data.DataStatus

class TableTestCellViewHolder(private val binding: TableViewCellLayoutBinding): AbstractViewHolder(binding.root) {
    companion object {
        fun create(parent: ViewGroup): TableTestCellViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = TableViewCellLayoutBinding.inflate(inflater, parent, false)
            return TableTestCellViewHolder(binding)
        }
    }

    /** Заполнить данные по ячейке */
    fun setCellData(cell: Cell?) {
        binding.cellText.text = cell?.value
        //задать цвет ячейки
        when {
            cell?.rowStatus == DataStatus.ROW_DELETE ->
                binding.root.setBackgroundResource(R.color.table_row_delete_color)
            cell?.status == DataStatus.EDIT || cell?.rowStatus == DataStatus.ROW_ADD ->
                binding.root.setBackgroundResource(R.color.table_cell_edit_color)
            else -> {
                binding.root.background = null
            }
        }
    }
}