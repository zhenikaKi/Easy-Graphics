package ru.easygraphics.mainWindow

import kotlinx.coroutines.launch
import ru.easygraphics.baseobjects.BaseViewModel
import ru.easygraphics.data.db.repositories.DataRepository
import ru.easygraphics.states.BaseState
import ru.easygraphics.states.ChartsListState

class ChartsListViewModel(private val repository: DataRepository) : BaseViewModel<BaseState>() {
    fun getChartsList() {
        liveData.postValue(BaseState.Loading)
        coroutineScope.launch {
            val data = repository.getChartsList()
            liveData.postValue(ChartsListState.Success(data))
        }
    }

    fun deleteChart(chartId: Long) {
        coroutineScope.launch {
            repository.deleteChart(chartId)
        }
    }
}