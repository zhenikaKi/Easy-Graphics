package ru.easygraphics.tabletest

import android.util.Log
import androidx.room.Transaction
import io.github.ekiryushin.scrolltableview.cell.Cell
import io.github.ekiryushin.scrolltableview.cell.CellView
import io.github.ekiryushin.scrolltableview.cell.DataStatus
import io.github.ekiryushin.scrolltableview.cell.RowCell
import kotlinx.coroutines.launch
import ru.easygraphics.baseobjects.BaseViewModel
import ru.easygraphics.data.db.entities.ChartAllDataViewed
import ru.easygraphics.data.db.entities.HorizontalValue
import ru.easygraphics.data.db.entities.VerticalValue
import ru.easygraphics.data.db.repositories.DataRepository
import ru.easygraphics.helpers.consts.App
import ru.easygraphics.helpers.consts.DB
import ru.easygraphics.states.BaseState
import ru.easygraphics.states.TableTestState

class TableTestViewModel(private val repository: DataRepository) : BaseViewModel<BaseState>() {

    private lateinit var graphicData: ChartAllDataViewed
    private var xAxisViewed = CellView.EDIT_STRING
    private var dataStatus = DataStatus.EDIT

    //сформировать данные для графика
    fun loadTableData(chartId: Long) {

        Log.d(App.LOG_TAG, "Log id $chartId")
        liveData.postValue(BaseState.Loading)
        coroutineScope.launch {
            graphicData = repository.getGraphicData(chartId = chartId)
            Log.d(App.LOG_TAG, "$graphicData")
            //сформируем формат для значений по оси X
            xAxisViewed = getXAxisViewed(graphicData.chart.xValueType)

            //сформируем шапку таблицы
            val columns: MutableList<Cell> = getTableColumns()
            val header = RowCell(columns)
            //сформируем основные данные для отображения
            val data: MutableList<RowCell> = getTableRowCells(xAxisViewed)

            liveData.postValue(TableTestState.LoadData(header, data, graphicData.chart.name))
        }
    }

    fun getXAxisViewed() = xAxisViewed

    private fun getXAxisViewed(xValueType: DB.ValueTypes): CellView {
        return when (xValueType) {
            DB.ValueTypes.NUMBER -> CellView.EDIT_NUMBER
            //дату будем обрабатывать как дд.мм.гггг, а уже потом показывать в нужном для пользователя виде
            DB.ValueTypes.DATE -> CellView.EDIT_DD_MM_YYYY
            else -> CellView.EDIT_STRING
        }
    }

    fun updateTableData(chartId: Long?, data: List<RowCell>?, linesId: List<Long?>?) {
        liveData.postValue(BaseState.Loading)
        coroutineScope.launch {
            updateTable(chartId, data, linesId)
            liveData.postValue(TableTestState.SavedData(chartId))
        }
    }

    @Transaction
    private suspend fun updateTable(chartId: Long?, data: List<RowCell>?, linesId: List<Long?>?) {

        insertRecords(chartId, data, linesId)

        updateRecords(chartId, data, linesId)

        deleteRecords(data)
    }

    private suspend fun deleteRecords(data: List<RowCell>?) {
        val deletedRows = data
            ?.filter { row -> row.status == DataStatus.DELETE }
            ?.mapNotNull { row -> row.columns[0].id }

        deletedRows?.let {
            Log.d(App.LOG_TAG, "Rows to delete $it")
            repository.deleteRows(it)
        }
    }

    private suspend fun insertRecords(chartId: Long?, data: List<RowCell>?, linesId: List<Long?>?) {
        dataStatus = DataStatus.ADD
        val addedRows = data?.filter { row -> row.status == dataStatus }

        Log.d(App.LOG_TAG, "Rows to insert $addedRows")

        linesId?.let { linId ->
            addedRows?.forEach { row ->
                val horizontalValue = getHorizontalValue(row.columns[0], chartId)
                val xValueId = repository.insertHorizontalValue(horizontalValue)

                xValueId?.let {
                    val verticalValues = getVerticalValues(row.columns, it, linId)
                    Log.d(App.LOG_TAG, "Insert horizontalValue: $horizontalValue")
                    Log.d(App.LOG_TAG, "Insert verticalValues: $verticalValues")
                    repository.insertVerticalValues(verticalValues)
                }
            }
        }
    }

    private suspend fun updateRecords(chartId: Long?, data: List<RowCell>?, linesId: List<Long?>?) {
        dataStatus = DataStatus.EDIT
        val updatedRows =
            data?.filter { row -> row.columns.any { column -> column.status == dataStatus } }

        linesId?.let { linId ->
            updatedRows?.forEach { row ->
                row.columns[0].id?.let {
                    val horizontalValue =
                        getHorizontalValue(row.columns[0], chartId)
                    val verticalValues = getVerticalValues(row.columns, it, linId)
                    Log.d(App.LOG_TAG, "Update horizontalValue: $horizontalValue")
                    Log.d(App.LOG_TAG, "Update verticalValues: $verticalValues")
                    repository.updateRowCells(horizontalValue, verticalValues)
                }
            }
        }
    }

    private fun getVerticalValues(
        cells: List<Cell>,
        xValueId: Long,
        linId: List<Long?>
    ): List<VerticalValue> {
        val verticalValues: MutableList<VerticalValue> = mutableListOf()

        for (ind in 1 until cells.size) {
            linId[ind]?.let { lineId ->
                if (isNeeToUpdateCell(cells[ind])) {
                    verticalValues.add(
                        VerticalValue(
                            yValueId = cells[ind].id,
                            lineId = lineId,
                            xValueId = xValueId,
                            value = cells[ind].value?.toDouble()
                        )
                    )
                }
            }
        }
        return verticalValues
    }

    private fun getHorizontalValue(cell: Cell, chartId: Long?): HorizontalValue? {
        var horizontalValue: HorizontalValue? = null
        if (isNeeToUpdateCell(cell)) {
            cell.value?.let {
                horizontalValue = HorizontalValue(
                    xValueId = cell.id,
                    chartId = chartId as Long,
                    value = it
                )
            }
        }
        return horizontalValue
    }

    private fun isNeeToUpdateCell(cell: Cell): Boolean {
        return if (dataStatus == DataStatus.ADD) {
            true
        } else {
            (cell.status == dataStatus && cell.id != null)
        }
    }

    private fun getTableColumns(): MutableList<Cell> {
        val columns: MutableList<Cell> = mutableListOf()
        columns.add(Cell(id = 0, value = graphicData.chart.xName))
        columns.addAll(graphicData.lines.map { line -> Cell(id = line.lineId, value = line.name) })
        return columns
    }

    private fun getTableRowCells(xAxisViewed: CellView): MutableList<RowCell> {
        return graphicData.values.map { hV ->
            val columnsData: MutableList<Cell> = mutableListOf()
            //добавляем колонку со значением по X
            columnsData.add(
                Cell(
                    id = hV.horizontalValue.xValueId,
                    value = hV.horizontalValue.value,
                    viewed = xAxisViewed
                )
            )
            //добавляем колонки со значениями по Y на каждой линии
            columnsData.addAll(hV.verticalValues.map { vV ->
                Cell(id = vV?.yValueId, value = vV?.value.toString(), viewed = CellView.EDIT_NUMBER)
            })
            RowCell(columnsData)
        }.toMutableList()
    }
}