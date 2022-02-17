package ru.easygraphics.tabletest

import kotlinx.coroutines.launch
import ru.easygraphics.baseobjects.BaseViewModel
import ru.easygraphics.data.db.entities.ChartAllDataViewed
import ru.easygraphics.states.BaseState
import ru.easygraphics.states.TableTestState
import ru.easygraphics.tabletest.data.Cell
import ru.easygraphics.tabletest.data.DataStatus
import ru.easygraphics.tabletest.data.RowHeaderCell

class TableTestViewModel(private val service: TableTestService) : BaseViewModel<BaseState>() {

    private lateinit var graphicData: ChartAllDataViewed

    //сформировать данные для графика
    fun loadTableData(chartId: Long) {
        liveData.postValue(BaseState.Loading)
        coroutineScope.launch {
            graphicData = service.getGraphicData(chartId = chartId)
            //сформируем шапку таблицы
            val columnHeaders = service.getColumnHeaders(graphicData)
            //сформируем список заголовка каждой строки
            val rowHeaders = service.getRowHeaders(graphicData)
            //сформируем основные данные для отображения
            val data = service.getTableRowCells(graphicData)

            liveData.postValue(TableTestState.LoadData(columnHeaders, rowHeaders, data, graphicData.chart.name))
        }
    }

    /** Добавить в таблицу новую строку */
    fun addNewRow(adapter: TableTestAdapter): Int {
        val rowPosition = adapter.getCountRows()
        val rowHeader = RowHeaderCell(value = rowPosition.toString())
        rowHeader.rowStatus = DataStatus.ROW_ADD
        val columns: MutableList<Cell> = mutableListOf()
        for (ind in 0 until adapter.getHeaders().size) {
            columns.add(Cell())
        }
        adapter.addDataRow(rowPosition, rowHeader, columns)
        return rowPosition
    }

    fun updateTableData(chartId: Long?, data: List<List<Cell>>, linesId: List<Long?>?) {
        liveData.postValue(BaseState.Loading)
        coroutineScope.launch {
            service.updateTable(chartId, data, linesId)
            liveData.postValue(TableTestState.SavedData(chartId))
        }
    }
}