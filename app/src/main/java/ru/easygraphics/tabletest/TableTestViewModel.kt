package ru.easygraphics.tabletest

import io.github.ekiryushin.scrolltableview.cell.Cell
import io.github.ekiryushin.scrolltableview.cell.CellView
import io.github.ekiryushin.scrolltableview.cell.RowCell
import kotlinx.coroutines.launch
import ru.easygraphics.baseobjects.BaseViewModel
import ru.easygraphics.data.db.entities.ChartAllDataViewed
import ru.easygraphics.data.db.repositories.DataRepository
import ru.easygraphics.states.BaseState
import ru.easygraphics.states.TableTestState

class TableTestViewModel(private val repository: DataRepository): BaseViewModel<BaseState>() {

    private lateinit var graphicData: ChartAllDataViewed

    //сформировать данные для графика
    fun loadTableData(chartId: Long) {
        liveData.postValue(BaseState.Loading())
        coroutineScope.launch {
            graphicData = repository.getGraphicData(chartId = chartId)

            //сформируем шапку таблицы
            val columns: MutableList<Cell> = getTableColumns()
            val header = RowCell(columns)
            //сформируем основные данные для отображения
            val data: MutableList<RowCell> = getTableRowCells()

            liveData.postValue(TableTestState.Success(header, data, graphicData.chart.name))
        }
    }

    private fun getTableColumns(): MutableList<Cell> {
        val columns: MutableList<Cell> = mutableListOf()
        columns.add(Cell(id = 0, value = graphicData.chart.xName))
        columns.addAll(graphicData.lines.map { line -> Cell(id = line.lineId, value = line.name) })
        return columns
    }

    private fun getTableRowCells(): MutableList<RowCell> {
        val result = graphicData.values.map { hV ->
            val columnsData: MutableList<Cell> = mutableListOf()
            //добавляем колонку со значением по X
            columnsData.add(Cell(id = hV.horizontalValue.xValueId, value = hV.horizontalValue.value, viewed = CellView.EDIT_STRING))
            //добавляем колонки со значениями по Y на каждой линии
            columnsData.addAll(hV.verticalValues.map { vV ->
                Cell(id = vV?.yValueId, value = vV?.value.toString(), viewed = CellView.EDIT_NUMBER)
            })
            RowCell(columnsData)
        }.toMutableList()

        return result
    }
}