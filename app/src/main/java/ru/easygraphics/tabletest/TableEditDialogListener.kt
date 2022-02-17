package ru.easygraphics.tabletest

import android.content.DialogInterface
import android.widget.LinearLayout

/** Слушатель кнопок диалогового окна редактирования строки таблицы */
interface TableEditDialogListener {
    /** Подтвеждение данных в диалоговом окне */
    fun applyDialog(view: LinearLayout)

    /** Отмена значений. */
    fun cancelDialog(dialog: DialogInterface, rowId: Int, asNewRow: Boolean)
}