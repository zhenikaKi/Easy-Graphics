package ru.easygraphics.data.db.repositories

import kotlinx.coroutines.*
import ru.easygraphics.data.db.AppDB
import ru.easygraphics.data.db.entities.*
import ru.easygraphics.helpers.ColorConvert

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

    override suspend fun saveChartDescription(
        chart: Chart,
        listYLines: List<Pair<String, Int>>
    ): Long {
        var chartId: Long
        if (chart.chartId == null) {
            chartId = db.chartDao().save(chart)
            for (i in listYLines.indices) {
                db.chartLineDao().save(
                    ChartLine(
                        null, chartId, listYLines[i].first,
                        ColorConvert.colorToHex(listYLines[i].second)
                    )
                )
            }
        } else {
            chartId = db.chartDao().save(chart)
            val chartLines = db.chartLineDao().getLines(chartId)
            for (i in listYLines.indices) {
                if (i >= chartLines.size) {
                    db.chartLineDao().save(
                        ChartLine(
                            null, chartId, listYLines[i].first,
                            ColorConvert.colorToHex(listYLines[i].second)
                        )
                    )
                } else {
                    db.chartLineDao().save(
                        ChartLine(
                            chartLines[i].lineId, chartId, listYLines[i].first,
                            ColorConvert.colorToHex(listYLines[i].second)
                        )
                    )
                }
            }
            if (listYLines.size < chartLines.size) {
                for (i in listYLines.size..chartLines.size - 1) {
                    db.chartLineDao().delete(
                        ChartLine(
                            chartLines[i].lineId, chartId, listYLines[i].first,
                            ColorConvert.colorToHex(listYLines[i].second)
                        )
                    )
                }
            }
        }
        return chartId
    }

    override suspend fun saveChart(chart: Chart) {
        db.chartDao().save(chart)
    }

    /** Получить конкретный график */
    override suspend fun getChart(chartId: Long): Chart = db.chartDao().getChart(chartId)

    /** Получить линии шрафика */
    override suspend fun getLines(chartId: Long): List<ChartLine> = db.chartLineDao().getLines(chartId)
}