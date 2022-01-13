package ru.easygraphics.tabletest

import android.util.Log
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
import ru.easygraphics.toHorizontalValue
import ru.easygraphics.toVerticalValue

class TableTestViewModel(private val repository: DataRepository) : BaseViewModel<BaseState>() {

    private lateinit var graphicData: ChartAllDataViewed
    private var xAxisViewed = CellView.EDIT_STRING

    //сформировать данные для графика
    fun loadTableData(chartId: Long) {
        liveData.postValue(BaseState.Loading())
        coroutineScope.launch {
            graphicData = repository.getGraphicData(chartId = chartId)
            //сформируем формат для значений по оси X
            xAxisViewed = getXAxisViewed(graphicData.chart.xValueType)

            //сформируем шапку таблицы
            val columns: MutableList<Cell> = getTableColumns()
            val header = RowCell(columns)
            //сформируем основные данные для отображения
            val data: MutableList<RowCell> = getTableRowCells(xAxisViewed)

            liveData.postValue(TableTestState.Success(header, data, graphicData.chart.name))
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

    fun updateTableData(chartId: Long?, data: List<RowCell>?) {
        liveData.postValue(BaseState.Loading())
        data?.let { it ->
            val editedData = it.filter { row ->
                row.status == DataStatus.ADD || row.status == DataStatus.DELETE
                        || row.columns.any { column -> column.status == DataStatus.EDIT }
            }

            insertTableLines(chartId, editedData.filter { rowCell ->
                rowCell.status == DataStatus.ADD
            })

            deleteTableLines(editedData.filter { rowCell ->
                rowCell.status == DataStatus.DELETE
            })

            chartId?.let {
                updateTableLines(chartId, editedData)
            }

        }
    }

    private fun updateTableLines(chartId: Long, editedData: List<RowCell>) {
        editedData.forEach { rowCell ->
            coroutineScope.launch {
                repository.updateVerticalValues(rowCell.toVerticalValue())
                repository.updateHorizontalValues(rowCell.toHorizontalValue(chartId))
            }
        }
    }

    private fun insertTableLines(chartId: Long?, insertData: List<RowCell>) {
        insertData.forEach { rowCell ->
            chartId?.let {
                coroutineScope.launch {
                    val insertResult = repository.insertHorizontalValue(
                        HorizontalValue(
                            xValueId = null,
                            chartId = chartId,
                            value = rowCell.columns[0].value as String
                        )
                    )

                    rowCell.columns.mapIndexed { index, cell ->
                        val verticalValues: MutableList<VerticalValue> = mutableListOf()
                        verticalValues.add(
                            VerticalValue(
                                yValueId = null,
                                lineId = index.toLong(),
                                xValueId = insertResult,
                                value = cell.value?.toDouble()
                            )
                        )
                        verticalValues
                    }

                    repository.insertVerticalValues(
                        getValuesToInsert(rowCell.columns, insertResult)
                    )
                }
            }
        }

    }

    private fun getValuesToInsert(columns: List<Cell>, insertResult: Long) =
        columns.mapIndexed { index, cell ->
            VerticalValue(
                yValueId = null,
                lineId = index.toLong(),
                xValueId = insertResult,
                value = cell.value?.toDouble()
            )
        }.toMutableList()

    private fun deleteTableLines(deleteData: List<RowCell>) {
        val rows = deleteData.map { rowCell ->
            rowCell.columns[0].id as Long
        }

        Log.d(App.LOG_TAG, rows.toString())

        coroutineScope.launch {
            repository.deleteHorizontalValue(rows)

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