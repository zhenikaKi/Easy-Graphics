package ru.easygraphics.baseobjects

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import ru.easygraphics.states.BaseState

/** Общая ViewModel с основной логикой инициализации */
abstract class BaseViewModel<S: BaseState>: ViewModel() {

    protected val liveData: MutableLiveData<S> = MutableLiveData()
    val coroutineScope: CoroutineScope by lazy {
        CoroutineScope(
            Dispatchers.IO
                    + SupervisorJob() //дочерние корутины выполняются независимо от ошибок в других корутинах
                    + CoroutineExceptionHandler { _, throwable -> handleCoroutineError(throwable) } //обработка ошибок в корутине
        )
    }

    //получить данные, на которые выполнена подписка
    open fun getLiveData(): LiveData<S> = liveData

    override fun onCleared() {
        //завершаем корутины
        coroutineScope.coroutineContext.cancelChildren()
    }

    //обработка ошибок в корутине
    open fun handleCoroutineError(throwable: Throwable) {
    }
}