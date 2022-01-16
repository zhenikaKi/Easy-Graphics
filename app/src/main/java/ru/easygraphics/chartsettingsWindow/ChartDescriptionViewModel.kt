package ru.easygraphics.chartsettingsWindow

import kotlinx.coroutines.launch
import ru.easygraphics.baseobjects.BaseViewModel
import ru.easygraphics.data.db.entities.Chart
import ru.easygraphics.data.db.entities.ChartLine
import ru.easygraphics.states.BaseState
import ru.easygraphics.states.DescriptionState
import ru.easygraphics.states.LoadingTypes

class ChartDescriptionViewModel(private val service: ChartDescriptionService):BaseViewModel<BaseState>() {

    fun saveGraphicData(
        chart: Chart,
        lines: List<ChartLine>,
        linesDelete: List<Long>?,
        openTableAfterSave: Boolean){

        if (openTableAfterSave) {
            liveData.postValue(BaseState.Loading(LoadingTypes.SAVED_WITH_TABLE_OPENING))
        }
        else {
            liveData.postValue(BaseState.Loading(LoadingTypes.SAVED))
        }
        coroutineScope.launch {
            val saved = service.saveDataToDB(chart, lines, linesDelete)

            if (openTableAfterSave) {
                liveData.postValue(DescriptionState.SavedForOpenTable(saved.first, saved.second))
            }
            else {
                liveData.postValue(DescriptionState.Saved(saved.first, saved.second))
            }
        }
    }

    //получить данные по графику для редактиования
    fun loadGraphicData(chartId: Long) {
        liveData.postValue(BaseState.Loading(LoadingTypes.ROOT_DATA))
        coroutineScope.launch {
            val loaded = service.getData(chartId = chartId)
            liveData.postValue(DescriptionState.LoadData(loaded.first, loaded.second))
        }
    }

    //очистка состояния
    fun clearState() {
        liveData.postValue(null)
    }

    override fun handleCoroutineError(throwable: Throwable) {
        super.handleCoroutineError(throwable)
        throwable.message?.let { liveData.postValue(BaseState.ErrorState(it)) }
    }
}