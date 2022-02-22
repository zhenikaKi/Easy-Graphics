package ru.easygraphics.tabletest

import kotlinx.coroutines.launch
import ru.easygraphics.baseobjects.BaseViewModel
import ru.easygraphics.data.db.entities.ChartAllDataViewed
import ru.easygraphics.helpers.consts.DB
import ru.easygraphics.states.BaseState
import ru.easygraphics.states.TableTestState
import ru.easygraphics.tabletest.data.Cell
import ru.easygraphics.tabletest.data.DataStatus
import ru.easygraphics.tabletest.data.RowHeaderCell

class TableTestViewModel(private val service: TableTestService) : BaseViewModel<BaseState>() {

    private var graphicData: ChartAllDataViewed? = null

    //сформировать данные для графика
    fun loadTableData(chartId: Long) {
        liveData.postValue(BaseState.Loading)
        coroutineScope.launch {
            graphicData = service.getGraphicData(chartId = chartId)
            var columnHeaders: List<Cell> = listOf()
            var rowHeaders: List<RowHeaderCell> = listOf()
            var data: List<List<Cell>> = listOf()

            graphicData?.let {
                //сформируем шапку таблицы
                columnHeaders = service.getColumnHeaders(it)
                //сформируем список заголовка каждой строки
                rowHeaders = service.getRowHeaders(it)
                //сформируем основные данные для отображения
                data = service.getTableRowCells(it)
            }

            liveData.postValue(TableTestState.LoadData(columnHeaders, rowHeaders, data, graphicData?.chart?.name))
        }
    }

    /** Получить тип подписи по оси X */
    fun getXValueType(): DB.ValueTypes? = graphicData?.chart?.xValueType

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