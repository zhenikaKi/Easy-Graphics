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
        db.chartDao().delete(db.chartDao().getChart(chartId))
    }

    override suspend fun saveChartDescription(
        chart: Chart,
        listYLines: List<Pair<String, Int>>
    ): Long {
        var chart_id: Long
        if (chart.chartId == null) {
            chart_id = db.chartDao().save(chart)
            for (i in listYLines.indices) {
                db.chartLineDao().save(
                    ChartLine(
                        null, chart_id, listYLines[i].first,
                        ColorConvert.colorToHex(listYLines[i].second)
                    )
                )
            }
        } else {
            chart_id = db.chartDao().save(chart)
            val cl = db.chartLineDao().getLines(chart_id)
            for (i in listYLines.indices) {
                if (i >= cl.size) {
                    db.chartLineDao().save(
                        ChartLine(
                            null, chart_id, listYLines[i].first,
                            ColorConvert.colorToHex(listYLines[i].second)
                        )
                    )
                } else {
                    db.chartLineDao().save(
                        ChartLine(
                            cl[i].lineId, chart_id, listYLines[i].first,
                            ColorConvert.colorToHex(listYLines[i].second)
                        )
                    )
                }
            }
            if (listYLines.size < cl.size) {
                for (i in listYLines.size..cl.size - 1) {
                    db.chartLineDao().delete(
                        ChartLine(
                            cl[i].lineId, chart_id, listYLines[i].first,
                            ColorConvert.colorToHex(listYLines[i].second)
                        )
                    )
                }
            }
        }
        return chart_id
    }

    override suspend fun saveChart(chart: Chart) {
        db.chartDao().save(chart)
    }
}