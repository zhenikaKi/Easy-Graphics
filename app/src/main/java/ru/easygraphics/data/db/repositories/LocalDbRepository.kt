package ru.easygraphics.data.db.repositories

import android.util.Log
import io.github.ekiryushin.scrolltableview.cell.RowCell
import kotlinx.coroutines.*
import ru.easygraphics.data.db.AppDB
import ru.easygraphics.data.db.entities.*
import ru.easygraphics.helpers.consts.App

class LocalDbRepository(private val db: AppDB) : DataRepository {

    override suspend fun getGraphicData(chartId: Long): ChartAllDataViewed {
        //сперва вытягиваем все данные по графику из базы
        val dataInDB = db.chartAllDataDao().getAllDataOnChartId(chartId)

        //преобразовываем данные к нужному виду
        val result = ChartAllDataViewed(chart = dataInDB.chart)
        Log.d(App.LOG_TAG, "Chart all data view $result")
        result.lines = dataInDB.lines.map { el -> el.chartLine }
        result.values = dataInDB.xValues.map { horizontalValue ->
            //получаем конкретное значение по X
            val xAndLines = HorizontalValueWithLinesValue(horizontalValue = horizontalValue)
            //для каждой линии ищем соотетствующее значение по Y
            xAndLines.verticalValues = dataInDB.lines.map mapForVertical@{ line ->
                val yForLine: VerticalValue? =
                    line.yValues.firstOrNull { verticalValue -> verticalValue.xValueId == horizontalValue.xValueId }
                return@mapForVertical yForLine
            }

            return@map xAndLines
        }

        return result
    }

    override suspend fun getAllDataOnChartId(chartId: Long): ChartAllData =
        db.chartAllDataDao().getAllDataOnChartId(chartId = chartId)

    override suspend fun getChartsList(): List<Pair<Long, String>> {
        val cl = db.chartDao().getCharts()
        return cl.map { chart -> Pair(chart.chartId!!, chart.name) }
    }

    override suspend fun deleteChart(chartId: Long) {
        db.chartDao().delete(chartId)
    }

    override suspend fun saveChart(chart: Chart): Long {
        chart.chartId?.let {
            db.chartDao().update(chart)
            return it
        } ?: let {
            return db.chartDao().insert(chart)
        }
    }


    /** Получить конкретный график */
    override suspend fun getChart(chartId: Long): Chart = db.chartDao().getChart(chartId)

    /** Получить линии графика */
    override suspend fun getLines(chartId: Long): List<ChartLine> =
        db.chartLineDao().getLines(chartId)

    /** Удалить линии графика */
    override suspend fun deleteLines(chartLinesId: List<Long>) {
        db.chartLineDao().delete(chartLinesId)
    }

    /** Сохранить линии графика */
    override suspend fun saveLines(lines: List<ChartLine>) {
        //отдельно сохраняем новые линии
        val newLines = lines.filter { line -> line.lineId == null }
        if (newLines.isNotEmpty()) {
            db.chartLineDao().insert(newLines)
        }

        //и отдельно - обновленные линии
        val updateLines = lines.filter { line -> line.lineId != null }
        if (updateLines.isNotEmpty()) {
            db.chartLineDao().update(updateLines)
        }
    }

    /** Удалить значения строки в таблице */
    override suspend fun deleteRows(xValuesId: List<Long>) {
        db.horizontalValueDao().deleteById(xValuesId)
    }

    /** Обновить значения строки в таблице */
    override suspend fun updateRowCells(horizontalValue: HorizontalValue?, verticalValues: List<VerticalValue>?) {
        horizontalValue?.let {
            db.horizontalValueDao().update(it)
        }
        verticalValues?.let {
            db.verticalValueDao().update(it)
        }
    }

    /** Сохранить изменения значения строки в таблице */
    override suspend fun insertHorizontalValue(horizontalValue: HorizontalValue?) =
        horizontalValue?.let {
            db.horizontalValueDao().insert(it)
        }

    /** Сохранить изменения значения строки в таблице */
    override suspend fun insertVerticalValues(verticalValues: List<VerticalValue>) {
        db.verticalValueDao().insert(verticalValues)
    }
}