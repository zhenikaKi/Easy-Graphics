package ru.easygraphics.tabletest

import android.util.Log
import androidx.room.Transaction
import ru.easygraphics.data.db.entities.ChartAllDataViewed
import ru.easygraphics.data.db.entities.HorizontalValue
import ru.easygraphics.data.db.entities.VerticalValue
import ru.easygraphics.data.db.repositories.DataRepository
import ru.easygraphics.helpers.consts.App
import ru.easygraphics.tabletest.data.Cell
import ru.easygraphics.tabletest.data.DataStatus
import ru.easygraphics.tabletest.data.RowHeaderCell

class TableTestService(private val repository: DataRepository) {

    /**
     * Получить все данные по графику в виде, удобном для редактирования.
     * @param chartId идентификатор конкретного графика
     */
    suspend fun getGraphicData(chartId: Long): ChartAllDataViewed =
        repository.getGraphicData(chartId = chartId)

    /** Сформировать заголовок каждого столбца */
    fun getColumnHeaders(graphicData: ChartAllDataViewed): List<Cell> {
        val headers: MutableList<Cell> = mutableListOf()
        headers.add(Cell(value = graphicData.chart.xName))
        headers.addAll(graphicData.lines.map { line -> Cell(id = line.lineId, value = line.name) })
        return headers
    }

    /** Сформировать список заголовков каждой строки (левый столбец) */
    fun getRowHeaders(graphicData: ChartAllDataViewed): List<RowHeaderCell> {
        val result: MutableList<RowHeaderCell> = mutableListOf()
        graphicData.values.forEachIndexed { index, _ ->
            result.add(RowHeaderCell(value = index.toString()))
        }
        return result
    }

    /** Сформировать содержимое таблицы */
    fun getTableRowCells(graphicData: ChartAllDataViewed): List<List<Cell>> {
        return graphicData.values.map { hV ->
            //добавляем колонку с подписью по оси X
            val columns: MutableList<Cell> = mutableListOf()
            columns.add(Cell(id = hV.horizontalValue.xValueId, value = hV.horizontalValue.value))
            //добавляем колонки со значениями по Y на каждой линии
            columns.addAll(hV.verticalValues.map { vV ->
                Cell(id = vV?.yValueId, value = vV?.value.toString())
            })
            return@map columns
        }.toList()
    }

    /** Сохранить данные */
    @Transaction
    suspend fun updateTable(chartId: Long?, data: List<List<Cell>>, linesId: List<Long?>?) {

        insertRecords(chartId, data, linesId)

        updateRecords(chartId, data, linesId)

        deleteRecords(data)
    }

    /** Добавить новые строки */
    private suspend fun insertRecords(chartId: Long?, data: List<List<Cell>>, linesId: List<Long?>?) {
        val addedRows = data.filter { row -> row[0].rowStatus == DataStatus.ROW_ADD }
        Log.d(App.LOG_TAG, "addedRows = $addedRows")

        linesId?.let { linId ->
            addedRows.forEach { row ->
                val horizontalValue = getHorizontalValue(row[0], chartId, DataStatus.ROW_ADD)
                val xValueId = repository.insertHorizontalValue(horizontalValue)

                xValueId?.let {
                    val verticalValues = getVerticalValues(row, it, linId, DataStatus.ROW_ADD)
                    Log.d(App.LOG_TAG, "horizontalValue = $horizontalValue")
                    Log.d(App.LOG_TAG, "verticalValues = $verticalValues")
                    repository.insertVerticalValues(verticalValues)
                }
            }
        }
    }

    private suspend fun updateRecords(chartId: Long?, data: List<List<Cell>>, linesId: List<Long?>?) {
        val updatedRows =
            data.filter { row -> row.any { column -> column.status == DataStatus.EDIT } }

        linesId?.let { linId ->
            updatedRows.forEach { row ->
                row[0].id?.let {
                    val horizontalValue =
                        getHorizontalValue(row[0], chartId, DataStatus.EDIT)
                    val verticalValues = getVerticalValues(row, it, linId, DataStatus.EDIT)
                    Log.d(App.LOG_TAG, "update horizontalValue = $horizontalValue")
                    Log.d(App.LOG_TAG, "update verticalValues = $verticalValues")
                    repository.updateRowCells(horizontalValue, verticalValues)
                }
            }
        }
    }

    /** Удалить строки таблицы */
    private suspend fun deleteRecords(data: List<List<Cell>>) {
        val deletedRows = data
            .filter { row -> row[0].rowStatus == DataStatus.ROW_DELETE }
            .mapNotNull { row -> row[0].id }

        Log.d(App.LOG_TAG, "deletedRows = $deletedRows")

        repository.deleteRows(deletedRows)
    }

    private fun getVerticalValues(cells: List<Cell>,
                                  xValueId: Long,
                                  linId: List<Long?>,
                                  checkStatus: DataStatus): List<VerticalValue> {
        val verticalValues: MutableList<VerticalValue> = mutableListOf()

        for (ind in 1 until cells.size) {
            linId[ind]?.let { lineId ->
                if (isNeeToUpdateCell(cells[ind], checkStatus)) {
                    verticalValues.add(
                        VerticalValue(
                            yValueId = cells[ind].id,
                            lineId = lineId,
                            xValueId = xValueId,
                            value = cells[ind].value?.toDoubleOrNull()
                        )
                    )
                }
            }
        }
        return verticalValues
    }

    private fun getHorizontalValue(cell: Cell,
                                   chartId: Long?,
                                   checkStatus: DataStatus): HorizontalValue? {
        var horizontalValue: HorizontalValue? = null
        if (isNeeToUpdateCell(cell, checkStatus)) {
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

    private fun isNeeToUpdateCell(cell: Cell, checkStatus: DataStatus): Boolean {
        return if (checkStatus == DataStatus.ROW_ADD) {
            true
        } else {
            (cell.status == checkStatus && cell.id != null)
        }
    }
}