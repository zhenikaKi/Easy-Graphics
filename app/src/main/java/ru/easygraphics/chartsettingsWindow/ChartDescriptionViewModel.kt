package ru.easygraphics.chartsettingsWindow

import kotlinx.coroutines.launch
import ru.easygraphics.baseobjects.BaseViewModel
import ru.easygraphics.data.db.entities.Chart
import ru.easygraphics.data.db.repositories.DataRepository
import ru.easygraphics.states.BaseState
import ru.easygraphics.states.DescriptionState
import ru.easygraphics.states.LoadingTypes

class ChartDescriptionViewModel(private val repository: DataRepository):BaseViewModel<BaseState>() {

    fun saveDataToDB(chart: Chart, list_y_lines: List<Pair<String, Int>>){
        liveData.postValue(BaseState.Loading(LoadingTypes.SAVED))
        coroutineScope.launch {
            val chartId = repository.saveChartDescription(chart, list_y_lines)
            liveData.postValue(DescriptionState.Success(chartId))
        }
    }

    //получить данные по графику для редактиования
    fun loadGraphicData(chartId: Long) {
        liveData.postValue(BaseState.Loading(LoadingTypes.ROOT_DATA))
        coroutineScope.launch {
            val chart = repository.getChart(chartId = chartId)
            val lines = repository.getLines(chartId = chartId)
            liveData.postValue(DescriptionState.LoadData(chart, lines))
        }
    }

    override fun handleCoroutineError(throwable: Throwable) {
        super.handleCoroutineError(throwable)
        throwable.message?.let { liveData.postValue(BaseState.ErrorState(it)) }
    }
}