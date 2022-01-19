package ru.easygraphics.states

import ru.easygraphics.settingwindow.SettingItemType

sealed class SettingState: BaseState {
    data class Success(val data: List<SettingItemType>): SettingState()
    object ProcessImportExport: SettingState()
    object ImportSuccess: SettingState()
    data class ExportSuccess(val fileName: String): SettingState()
}
