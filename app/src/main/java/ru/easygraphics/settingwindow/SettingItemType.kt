package ru.easygraphics.settingwindow

import androidx.viewbinding.ViewBinding

/** Виды элементов списка в настройках. */
interface SettingItemType {
    companion object {
        //самый обычный элемент, содержащий заголовок и описание
        const val MAIN_TYPE = 1
        //экспорт данных
        const val EXPORT_TYPE = 2
        //импорт данных
        const val IMPORT_TYPE = 3
    }

    /** Получить вид элемента. */
    fun getType(): Int
}