package ru.easygraphics.helpers

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.text.InputType
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.LinearLayout
import com.google.android.material.textfield.TextInputLayout
import ru.easygraphics.R
import ru.easygraphics.extensions.asDDMMYYYY
import ru.easygraphics.extensions.setTextIfValueAsDDMMYYYY
import ru.easygraphics.extensions.setTextIfValueAsFloat
import ru.easygraphics.helpers.consts.App
import ru.easygraphics.tabletest.TableEditDialogListener
import ru.easygraphics.tabletest.data.Cell
import java.text.SimpleDateFormat
import java.util.*

/** Создание диалоговых окнон */
object AlertDialogs {
    /** Подготовить диалоговое окно с крутилкой */
    fun createLoading(context: Context, inflater: LayoutInflater): AlertDialog {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        val viewDialog = inflater.inflate(R.layout.dialog_progress_bar, null)
        builder
            .setCancelable(false)
            .setView(viewDialog)
        return builder.create()
    }

    /** Подготовить диалоговое окно с сообщением */
    fun createMessage(context: Context, text: String): AlertDialog {
        return AlertDialog.Builder(context)
            .setTitle(R.string.dialog_title_info)
            .setMessage(text)
            .setPositiveButton(context.getString(R.string.dialog_close_button)) { dialog, _ -> dialog.cancel() }
            .create()
    }

    /**
     * Подготовить диалоговое окно редактирования данных в таблице.
     * @param context приложения.
     * @param rowId строка, по которой нажали.
     * @param columns список ячеек в строке.
     * @param headers список ячеек заголовка таблицы.
     * @param listener слушатель нажатия кнопок диалогового окна
     * @param asNewRow диалоговое окно создается для новой строки или нет
     */
    fun createEditRowTable(context: Context,
                           rowId: Int,
                           columns: List<Cell>,
                           headers: List<Cell>?,
                           asNewRow: Boolean = false,
                           listener: TableEditDialogListener): AlertDialog {
        val inflater = LayoutInflater.from(context)
        val viewDialog = inflater.inflate(R.layout.table_dialog_edit_view, null)
        val block = viewDialog.findViewById<LinearLayout>(R.id.edit_value_block)
        block.setTag(R.id.tag_row_id, rowId)
        columns.forEachIndexed { index, cell ->
            val view = inflater.inflate(R.layout.table_item_edit_view, null)
            val editText: EditText = view.findViewById(R.id.edit_value)
            //задаем заголовок
            headers?.get(index)?.value?.let { title ->
                view.findViewById<TextInputLayout>(R.id.input_value).hint = title
            }
            //задаем значение
            setEditTextValue(context, editText, index == 0, cell.value)

            //задаем параметры, чтобы потом по ним обновить данные в таблице
            editText.setTag(R.id.tag_column_id, cell.id)
            block.addView(view)
        }

        return AlertDialog.Builder(context)
            .setTitle(R.string.table_row_edit)
            .setView(viewDialog)
            .setPositiveButton(context.getString(R.string.dialog_positive_button)) { _, _ ->
                listener.applyDialog(block) }
            .setNegativeButton(context.getString(R.string.dialog_negative_button)) { dialog, _ ->
                listener.cancelDialog(dialog, rowId, asNewRow)
            }
            .create()
    }

    /**
     * Заполнить поле ввода значением и задать тип поля ввода.
     * @param context приложения.
     * @param editText поле ввода.
     * @param isFirstValue первое или нет значение в списке. Если первое, то это подпись по оси X.
     * @param value значение для поля ввода
     */
    private fun setEditTextValue(context: Context, editText: EditText, isFirstValue: Boolean, value: String?) {
        if (isFirstValue) {
            setEditTextAsDate(context, editText, value)
        }
        else {
            editText.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED
            editText.setTextIfValueAsFloat(value)
        }
    }

    /**
     * Задать поле ввода как дату.
     * @param context приложения.
     * @param editText поле ввода.
     * @param value значение, которое нужно подставить в поле ввода.
     */
    private fun setEditTextAsDate(context: Context, editText: EditText, value: String?) {
        //сформируем дату календаря
        val calendar = getGregorianCalendar(value)

        editText.isFocusableInTouchMode = false
        editText.isLongClickable = false
        editText.setOnClickListener { showDialogSelectDate(context, editText, calendar) }
        editText.setTextIfValueAsDDMMYYYY(value)
    }

    /**
     * Сформировать грегорианский календарь из строки.
     * @param value строковая дата, из которой нужно сделать календарь.
     */
    private fun getGregorianCalendar(value: String?): GregorianCalendar {
        return if (value?.asDDMMYYYY() == true) {
            val date = SimpleDateFormat(App.DATE_FORMAT, Locale.getDefault()).parse(value)
            date?.let {
                val calendar = Calendar.getInstance()
                calendar.time = it
                GregorianCalendar(
                    calendar[Calendar.YEAR],
                    calendar[Calendar.MONTH],
                    calendar[Calendar.DAY_OF_MONTH],
                    0,
                    0,
                    0)
            } ?: GregorianCalendar(TimeZone.getDefault())
        }
        else {
            GregorianCalendar(TimeZone.getDefault())
        }
    }

    /**
     * Показать окно выбора даты.
     * @param context приложения.
     * @param editText поле ввода, для которого нужно показать окно.
     * @param gregorianCalendar дата, которую нужно выделить в календаре.
     */
    private fun showDialogSelectDate(context: Context, editText: EditText, gregorianCalendar: GregorianCalendar) {
        DatePickerDialog(
            context,
            R.style.DatePickerStyleDialog,
            { _, year, monthOfYear, dayOfMonth ->
                val selectedDate = String.format("%02d.%02d.%04d", dayOfMonth, monthOfYear+1, year)
                editText.setText(selectedDate)
            },
            gregorianCalendar.get(Calendar.YEAR),
            gregorianCalendar.get(Calendar.MONTH),
            gregorianCalendar.get(Calendar.DAY_OF_MONTH))
            .show()
    }
}