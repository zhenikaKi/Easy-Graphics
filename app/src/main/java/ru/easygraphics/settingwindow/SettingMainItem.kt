package ru.easygraphics.settingwindow

/** Самый обычный элемент, содержащий заголовок и описание. */
data class SettingMainItem(
    val title: String,
    val description: String,
    val itemType: Int = SettingItemType.MAIN_TYPE
): SettingItemType {
    /** Получить вид элемента. */
    override fun getType() = itemType
}
