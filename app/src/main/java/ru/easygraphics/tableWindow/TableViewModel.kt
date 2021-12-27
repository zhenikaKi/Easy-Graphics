package ru.easygraphics.tableWindow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.easygraphics.data.db.repositories.TableRowRepository
import ru.easygraphics.data.domain.TableLineData
import ru.easygraphics.parseToListOfTableLineData
import ru.easygraphics.states.Success

class TableViewModel(
    //private val app: Application,
    private val repository: TableRowRepository
) : ViewModel() {

    private val _rowData = MutableStateFlow<List<TableLineData>>(listOf())
    private val _error = MutableSharedFlow<String>()

    val rowData: Flow<List<TableLineData>> = _rowData
    val error: Flow<String> = _error

    fun fetchTableRows(chartId: Long) =
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = repository.getAllDataOnChartId(chartId = chartId)) {
                is Success -> {
                    _rowData.value = result.value.parseToListOfTableLineData()
                }
                is Error -> {
                    //_error.emit(app.getString(R.string.db_read_error))
                    _error.emit("Temp error")
                }
            }
        }
}