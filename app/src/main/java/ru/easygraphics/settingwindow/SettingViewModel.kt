package ru.easygraphics.settingwindow

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.launch
import ru.easygraphics.baseobjects.BaseViewModel
import ru.easygraphics.states.BaseState
import ru.easygraphics.states.SettingState

class SettingViewModel(private val service: SettingService): BaseViewModel<BaseState>() {
    /** Сформировать список настроек */
    fun getItemSettings(context: Context) {
        liveData.postValue(BaseState.LoadingRoot)
        coroutineScope.launch {
            val data = service.getItems(context)
            liveData.postValue(SettingState.Success(data))
        }
    }

    /**
     * Выполнить импорт данных.
     */
    fun importGraphics(context: Context) {
        liveData.postValue(SettingState.ProcessImportExport)
        coroutineScope.launch {
            service.importGraphics(context)
            liveData.postValue(SettingState.ImportSuccess)
        }
    }

    override fun handleCoroutineError(throwable: Throwable) {
        super.handleCoroutineError(throwable)
        throwable.message?.let { liveData.postValue(BaseState.ErrorState(it)) }
    }
}