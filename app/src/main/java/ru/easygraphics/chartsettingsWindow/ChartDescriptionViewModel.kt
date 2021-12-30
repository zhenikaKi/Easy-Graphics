package ru.easygraphics.chartsettingsWindow

import androidx.room.Transaction
import kotlinx.coroutines.launch
import ru.easygraphics.baseobjects.BaseViewModel
import ru.easygraphics.data.db.entities.Chart
import ru.easygraphics.data.db.entities.ChartLine
import ru.easygraphics.data.db.repositories.DataRepository
import ru.easygraphics.states.BaseState
import ru.easygraphics.states.DescriptionState
import ru.easygraphics.states.LoadingTypes

class ChartDescriptionViewModel(private val repository: DataRepository):BaseViewModel<BaseState>() {

    @Transaction
    fun saveDataToDB(
        chart: Chart,
        lines: List<ChartLine>,
        linesDelete: List<Long>?,
        openTableAfterSave: Boolean){
        liveData.postValue(BaseState.Loading(LoadingTypes.SAVED))
        coroutineScope.launch {
            //сперва удаляем линии
            linesDelete?.let { repository.deleteLines(it) }

            //сохраняем график
            val chartId = repository.saveChart(chart)
            val chartSaved = Chart(chartId, chart)

            //сохраняем линии
            lines.forEach { line -> line.chartId = chartId }
            repository.saveLines(lines)
            //нет возможности сразу получить обновленные сущности, поэтому приходится их отдельно доставать
            val linesSaved = repository.getLines(chartId)

            if (openTableAfterSave) {
                liveData.postValue(DescriptionState.SavedForOpenTable(chartSaved, linesSaved))
            }
            else {
                liveData.postValue(DescriptionState.Saved(chartSaved, linesSaved))
            }
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

    //очистка состояния
    fun clearState() {
        //liveData.value = null
        liveData.postValue(null)
    }

    override fun handleCoroutineError(throwable: Throwable) {
        super.handleCoroutineError(throwable)
        throwable.message?.let { liveData.postValue(BaseState.ErrorState(it)) }
    }
}