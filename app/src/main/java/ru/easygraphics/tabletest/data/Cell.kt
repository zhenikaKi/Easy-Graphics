package ru.easygraphics.tabletest.data

import com.evrencoskun.tableview.filter.IFilterableModel
import com.evrencoskun.tableview.sort.ISortableModel

data class Cell (
    /** Какой-то идентификатор значения, который можно использовать для сохранения в базу. */
    var id: Long? = null,

    /** Значение ячейки. */
    var value: String? = null,

    /**
     * Состояние значения строки, в которой расположеная данная иконка.
     * * DataStatus.NORMAL - строка из базы. Значение по умолчанию.
     * * DataStatus.ROW_DELETE - удаленная строка.
     * * DataStatus.ROW_ADD - новая строка.
     */

    var rowStatus: DataStatus = DataStatus.NORMAL
): ISortableModel, IFilterableModel {
    /**
     * Состояние значения ячейки.
     * * DataStatus.NORMAL - значение без изменения. Значение по умолчанию.
     * * DataStatus.EDIT - значение изменилось.
     * * DataStatus.ADD - значение добавилось.
     */
    var status: DataStatus = DataStatus.NORMAL

    /** Первоначальное значение для выставления корректного состояния. */
    private val initialValue: String? = value

    override fun getId() = id.toString()

    override fun getContent() = value

    override fun getFilterableKeyword() = value ?: "null"

    /** Обновить значение ячейки */
    fun updateValue(value: String) {
        this.value = value
        if (status != DataStatus.ROW_ADD) {
            status = if (value == initialValue) {
                DataStatus.NORMAL
            } else {
                DataStatus.EDIT
            }
        }
    }

}