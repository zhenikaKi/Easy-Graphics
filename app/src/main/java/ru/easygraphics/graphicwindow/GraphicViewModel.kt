package ru.easygraphics.graphicwindow

import kotlinx.coroutines.launch
import ru.easygraphics.baseobjects.BaseViewModel
import ru.easygraphics.data.db.repositories.DataRepository
import ru.easygraphics.states.BaseState
import ru.easygraphics.states.GraphicState

class GraphicViewModel(private val repository: DataRepository): BaseViewModel<BaseState>() {

    //получить все данные по графику
    fun loadGraphicData(chartId: Long) {
        liveData.postValue(BaseState.Loading)
        coroutineScope.launch {
            //получаем все данные по графику из базы
            val graphicData = repository.getGraphicData(chartId)
            liveData.postValue(GraphicState.Success(graphicData))
        }
    }

    //обработка ошибок внутри корутин
    override fun handleCoroutineError(throwable: Throwable) {
        super.handleCoroutineError(throwable)
        throwable.message?.let { liveData.postValue(BaseState.ErrorState(it)) }

    }
}