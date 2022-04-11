package ru.easygraphics.tabletest.data

/** Состояния значения ячейки или строки. */
enum class DataStatus {
    /** Строка или значение без изменений. */
    NORMAL,

    /** Измененное значение. */
    EDIT,

    /** Добавленная строка. */
    ROW_ADD,

    /** Удаленная строка. */
    ROW_DELETE
}