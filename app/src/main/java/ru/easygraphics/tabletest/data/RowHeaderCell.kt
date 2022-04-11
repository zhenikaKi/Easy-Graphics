package ru.easygraphics.tabletest.data

import com.evrencoskun.tableview.filter.IFilterableModel
import com.evrencoskun.tableview.sort.ISortableModel

data class RowHeaderCell (
    /** Значение ячейки. */
    var value: String? = null
): ISortableModel, IFilterableModel {
    /** Состояние строки.
     * * DataStatus.NORMAL - строка без изменения. Значение по умолчанию.
     * * DataStatus.ADD - строка добавилось.
     * * DataStatus.DELETE - строка удалилась.
     */
    var rowStatus: DataStatus = DataStatus.NORMAL

    /** Состояние, которое было перед удалением строки. */
    var statusBeforeDelete: DataStatus = DataStatus.NORMAL

    override fun getId() = value ?: "null"

    override fun getContent() = value

    override fun getFilterableKeyword() = value ?: "null"
}