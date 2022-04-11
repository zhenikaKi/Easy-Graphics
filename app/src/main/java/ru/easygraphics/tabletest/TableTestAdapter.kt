package ru.easygraphics.tabletest

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.evrencoskun.tableview.adapter.AbstractTableAdapter
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder
import ru.easygraphics.R
import ru.easygraphics.tabletest.data.Cell
import ru.easygraphics.tabletest.data.DataStatus
import ru.easygraphics.tabletest.data.RowHeaderCell
import ru.easygraphics.tabletest.viewholders.TableTestCellViewHolder
import ru.easygraphics.tabletest.viewholders.TableTestHeaderViewHolder
import ru.easygraphics.tabletest.viewholders.TableTestRowHeaderViewHolder

class TableTestAdapter(private val rowEventListener: RowEventListener): AbstractTableAdapter<Cell, RowHeaderCell, Cell>(){

    override fun onCreateCellViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder {
        return TableTestCellViewHolder.create(parent)
    }

    override fun onCreateColumnHeaderViewHolder(parent: ViewGroup,  viewType: Int): AbstractViewHolder {
        return TableTestHeaderViewHolder.create(parent)
    }

    override fun onCreateRowHeaderViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder {
        return TableTestRowHeaderViewHolder.create(parent, rowEventListener)
    }

    override fun onCreateCornerView(parent: ViewGroup): View {
        return LayoutInflater.from(parent.context)
            .inflate(R.layout.table_view_corner_layout, parent, false)
    }

    /** Отображение ячейки таблицы */
    override fun onBindCellViewHolder(holder: AbstractViewHolder,
                                      cellItemModel: Cell?,
                                      columnPosition: Int,
                                      rowPosition: Int) {
        (holder as TableTestCellViewHolder).setCellData(cellItemModel)
    }

    /** Отображение заголовка таблицы */
    override fun onBindColumnHeaderViewHolder(holder: AbstractViewHolder,
                                              columnHeaderItemModel: Cell?,
                                              position: Int) {
        (holder as TableTestHeaderViewHolder).setCellData(columnHeaderItemModel)
    }

    /** Отображение левой непрокручиваемой  ячейки */
    override fun onBindRowHeaderViewHolder(holder: AbstractViewHolder,
                                           rowHeaderItemModel: RowHeaderCell?,
                                           rowPosition: Int) {
        (holder as TableTestRowHeaderViewHolder).setCellData(rowHeaderItemModel, rowPosition)
    }

    /** Обновить ячейку левой непрокручиваемой ячейки */
    override fun changeRowHeaderItem(rowPosition: Int, rowHeaderModel: RowHeaderCell?) {
        super.changeRowHeaderItem(rowPosition, rowHeaderModel)

        //проставляем пометку о том, что строка удалена или восстановлена
        val cellRowItems = getCellRowItems(rowPosition)
        cellRowItems?.forEachIndexed { index, cell ->
            cell.rowStatus = rowHeaderModel?.rowStatus ?: DataStatus.NORMAL
            changeCellItem(index, rowPosition, cell)
        }
    }

    /** Получить значения заголовка */
    fun getHeaders(): List<Cell> = super.mColumnHeaderItems

    /** Получить все записи */
    fun getAllCell(): List<List<Cell>> = super.mCellItems

    /** Получить количество строк */
    fun getCountRows() = mCellItems.size

    /** Добавить новую сроку в конец */
    fun addDataRow(rowPosition: Int, rowHeader: RowHeaderCell, columns: List<Cell>) {
        columns.forEach { it.rowStatus = DataStatus.ROW_ADD }
        super.mRowHeaderItems.add(rowHeader)
        super.mCellItems.add(columns)
        addRow(rowPosition, rowHeader, columns)
    }
}