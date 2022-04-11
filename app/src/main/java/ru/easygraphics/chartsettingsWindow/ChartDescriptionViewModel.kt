package ru.easygraphics.chartsettingsWindow

import kotlinx.coroutines.launch
import ru.easygraphics.baseobjects.BaseViewModel
import ru.easygraphics.data.db.entities.Chart
import ru.easygraphics.data.db.entities.ChartLine
import ru.easygraphics.states.BaseState
import ru.easygraphics.states.DescriptionState

class ChartDescriptionViewModel(private val service: ChartDescriptionService):BaseViewModel<BaseState>() {

    fun saveGraphicData(
        chart: Chart,
        lines: List<ChartLine>,
        linesDelete: List<Long>?){
        liveData.postValue(BaseState.LoadingSaved)
        coroutineScope.launch {
            val saved = service.saveDataToDB(chart, lines, linesDelete)
            liveData.postValue(DescriptionState.Saved(saved.first, saved.second))
        }
    }

    //получить данные по графику для редактиования
    fun loadGraphicData(chartId: Long) {
        liveData.postValue(BaseState.LoadingRoot)
        coroutineScope.launch {
            val loaded = service.getData(chartId = chartId)
            liveData.postValue(DescriptionState.LoadData(loaded.first, loaded.second))
        }
    }

    //очистка состояния
    fun clearState() {
        liveData.postValue(BaseState.Null)
    }

    override fun handleCoroutineError(throwable: Throwable) {
        super.handleCoroutineError(throwable)
        throwable.message?.let { liveData.postValue(BaseState.ErrorState(it)) }
    }
}