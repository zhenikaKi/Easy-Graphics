package ru.easygraphics.tabletest.viewholders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder
import ru.easygraphics.R
import ru.easygraphics.databinding.TableViewRowHeaderLayoutBinding
import ru.easygraphics.tabletest.RowEventListener
import ru.easygraphics.tabletest.data.DataStatus
import ru.easygraphics.tabletest.data.RowHeaderCell

class TableTestRowHeaderViewHolder(private val binding: TableViewRowHeaderLayoutBinding,
                                   private val rowEventListener: RowEventListener): AbstractViewHolder(binding.root) {
    companion object {
        fun create(parent: ViewGroup, rowEventListener: RowEventListener): TableTestRowHeaderViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = TableViewRowHeaderLayoutBinding.inflate(inflater, parent, false)
            return TableTestRowHeaderViewHolder(binding, rowEventListener)
        }
    }

    /** Заполнить данные по заголоку */
    fun setCellData(rowHeaderItemModel: RowHeaderCell?, position: Int) {
        binding.rowHeaderText.text = position.toString()
        setClickEventListener(position, binding.root)
        setIconAndColor(rowHeaderItemModel)
    }

    private fun setIconAndColor(rowHeaderItemModel: RowHeaderCell?) {
        rowHeaderItemModel?.let {
            with(binding) {
                val rowDeleted = it.rowStatus == DataStatus.ROW_DELETE
                when {
                    it.rowStatus == DataStatus.ROW_ADD -> root.setBackgroundResource(R.color.table_cell_edit_color)
                    rowDeleted -> root.setBackgroundResource(R.color.table_row_delete_color)
                    else -> root.background = null
                }
                eventDelete.isVisible = !rowDeleted
                eventRestore.isVisible = rowDeleted
            }
        }
    }

    /**
     * Навешать обработчики на иконки удаления и восстановления строки.
     * @param rowId порядковый номер строки.
     * @param view ячейки с порядковым номером.
     */
    private fun setClickEventListener(rowId: Int, view: View) {
        val iconDelete: ImageView = view.findViewById(R.id.event_delete)
        val iconRestore: ImageView = view.findViewById(R.id.event_restore)

        iconDelete.setOnClickListener { _ -> rowEventListener.removeRow(rowId) }
        iconRestore.setOnClickListener { _ -> rowEventListener.restoreRow(rowId) }
    }
}