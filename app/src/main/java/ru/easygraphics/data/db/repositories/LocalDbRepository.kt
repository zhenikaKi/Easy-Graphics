package ru.easygraphics.data.db.repositories

import kotlinx.coroutines.*
import ru.easygraphics.data.db.AppDB
import ru.easygraphics.data.db.entities.*

class LocalDbRepository(private val db: AppDB) : DataRepository {

    override suspend fun getGraphicData(chartId: Long): ChartAllDataViewed {
        //сперва вытягиваем все данные по графику из базы
        val dataInDB = db.chartAllDataDao().getAllDataOnChartId(chartId)

        //преобразовываем данные к нужному виду
        val result = ChartAllDataViewed(chart = dataInDB.chart)
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

    override suspend fun getChartsList(): List<Pair<Long, String>> {
        val cl = db.chartDao().getCharts()
        return cl.map { chart -> Pair(chart.chartId!!, chart.name) }
    }

    override suspend fun deleteChart(chartId: Long) {
        db.chartDao().delete(chartId)
    }

    override suspend fun saveChart(chart: Chart): Long = db.chartDao().save(chart)

    /** Получить конкретный график */
    override suspend fun getChart(chartId: Long): Chart = db.chartDao().getChart(chartId)

    /** Получить линии графика */
    override suspend fun getLines(chartId: Long): List<ChartLine> = db.chartLineDao().getLines(chartId)

    /** Удалить линии графика */
    override suspend fun deleteLines(chartLinesId: List<Long>) {
        db.chartLineDao().delete(chartLinesId)
    }

    /** Сохранить линии графику */
    override suspend fun saveLines(lines: List<ChartLine>): List<Long> = db.chartLineDao().save(lines)
}