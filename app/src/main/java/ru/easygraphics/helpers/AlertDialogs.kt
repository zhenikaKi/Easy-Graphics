package ru.easygraphics.helpers

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import ru.easygraphics.R

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
}