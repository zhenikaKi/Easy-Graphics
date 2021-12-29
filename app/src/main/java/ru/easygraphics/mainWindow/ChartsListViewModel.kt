package ru.easygraphics.mainWindow

import kotlinx.coroutines.launch
import ru.easygraphics.baseobjects.BaseViewModel
import ru.easygraphics.data.db.repositories.DataRepository
import ru.easygraphics.states.BaseState
import ru.easygraphics.states.ChartsListState

class ChartsListViewModel(val repository: DataRepository) : BaseViewModel<BaseState>() {
    fun getChartsList() {
        liveData.postValue(BaseState.Loading())
        coroutineScope.launch {
            val l = repository.getChartsList()
            liveData.postValue(ChartsListState.Success(l))
        }
    }

    fun deleteChart(chartId: Long) {
        coroutineScope.launch {
            repository.deleteChart(chartId)
        }
    }
}