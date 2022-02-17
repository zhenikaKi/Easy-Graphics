package ru.easygraphics.tabletest

/** Слушатель удаления и восстановления строки */
interface RowEventListener {
    /** Удалить строку с данными по индексу строки */
    fun removeRow(rowId: Int)

    /** Восстановить строку */
    fun restoreRow(rowId: Int)
}