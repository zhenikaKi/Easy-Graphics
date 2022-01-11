package ru.easygraphics.tableWindow

import kotlinx.coroutines.launch
import ru.easygraphics.baseobjects.BaseViewModel
import ru.easygraphics.data.db.repositories.DataRepository
import ru.easygraphics.states.BaseState
import ru.easygraphics.states.TableState

class TableViewModel(private val repository: DataRepository) : BaseViewModel<BaseState>() {

    fun fetchTableRows(chartId: Long) {
        liveData.postValue(BaseState.Loading())
        coroutineScope.launch {
            val tableData = repository.getAllDataOnChartId(chartId)
            liveData.postValue(TableState.Success(tableData))
        }
    }

    override fun handleCoroutineError(throwable: Throwable) {
        super.handleCoroutineError(throwable)
        throwable.message?.let { liveData.postValue(BaseState.ErrorState(it)) }
    }
}