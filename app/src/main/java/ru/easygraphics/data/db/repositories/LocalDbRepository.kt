package ru.easygraphics.data.db.repositories

import kotlinx.coroutines.*
import ru.easygraphics.data.db.AppDB
import ru.easygraphics.data.db.entities.*
import ru.easygraphics.helpers.ColorConvert

class LocalDbRepository(private val db: AppDB): DataRepository {

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
            xAndLines.verticalValues = dataInDB.lines.map mapForVertical@ { line ->
                val yForLine: VerticalValue? =
                    line.yValues.firstOrNull { verticalValue -> verticalValue.xValueId == horizontalValue.xValueId }
                return@mapForVertical yForLine
            }

            return@map xAndLines
        }

        return result
    }

    override suspend fun saveChartDescription(chart: Chart, list_y_lines: List<Pair<String, Int>>):Long {
        var chart_id:Long
            if (chart.chartId == null) {
                chart_id=db.chartDao().save(chart)
                for (i in list_y_lines.indices) {
                    db.chartLineDao().save(ChartLine(null, chart_id,list_y_lines[i].first,
                        ColorConvert.colorToHex(list_y_lines[i].second)))
                }
            } else {
                chart_id=db.chartDao().save(chart)
                val cl=db.chartLineDao().getLines(chart_id)
                for (i in list_y_lines.indices) {
                    if (i>=cl.size) {
                        db.chartLineDao().save(ChartLine(null, chart_id,list_y_lines[i].first,
                            ColorConvert.colorToHex(list_y_lines[i].second)))
                    }
                    else{
                        db.chartLineDao().save(ChartLine(cl[i].lineId, chart_id,list_y_lines[i].first,
                            ColorConvert.colorToHex(list_y_lines[i].second)))
                    }
                }
                if (list_y_lines.size < cl.size){
                    for (i in list_y_lines.size..cl.size-1){
                        db.chartLineDao().delete(ChartLine(cl[i].lineId, chart_id,list_y_lines[i].first,
                            ColorConvert.colorToHex(list_y_lines[i].second)))
                    }
                }
        }
        return chart_id
    }

    override suspend fun saveChart(chart: Chart) {
        db.chartDao().save(chart)
    }
}