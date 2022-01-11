package ru.easygraphics.settingwindow

/** События адаптера настроек. */
interface SettingAdapterListener {
    /** Выполнить экспорт данных. */
    fun exportGraphics()

    /** Выполнить импорт графиков. */
    fun importGraphics()
}