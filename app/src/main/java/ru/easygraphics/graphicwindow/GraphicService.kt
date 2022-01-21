package ru.easygraphics.graphicwindow

import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import ru.easygraphics.data.db.entities.Chart
import ru.easygraphics.data.db.entities.ChartAllDataViewed
import ru.easygraphics.data.db.repositories.DataRepository
import ru.easygraphics.helpers.ColorConvert
import ru.easygraphics.helpers.XValueFormatter
import ru.easygraphics.helpers.consts.App
import ru.easygraphics.helpers.consts.DB
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.max

/** Сервис для обработки данных по отображению графику */
class GraphicService(private val repository: DataRepository) {

    companion object {
        //примерное количество точек на графике по оси X
        private const val APPROXIMATE_QUANTITY_POINT = 25
        //размер масштаба графика по умолчанию
        private const val DEFAULT_ZOOM_SCALE = 1f
        //толщина линии на графике
        private const val LINE_WIDTH = 2.5f
        //размер точки линии на графике
        private const val LINE_POINT_SIZE = 4f
        //размер текста значения около точки линии на графике
        private const val LINE_POINT_VALUE_SIZE = 10f
    }

    /**
     * Получить данные по графику из базы.
     * @param chartId идентифкатор графика.
     * @return данные, содержащие все информацию по графику.
     */
    suspend fun getGraphicData(chartId: Long): ChartGraphicDto {
        val chartAllData = repository.getGraphicData(chartId)
        return mapDataToViewed(chartAllData)
    }

    /**
     * Преобразовать данные графика, полученные из базы к данным для отображения.
     * @param data данные графика, полученные из базы.
     * @return готовые данные для отображения.
     */
    private fun mapDataToViewed(data: ChartAllDataViewed): ChartGraphicDto {
        //сформируем список подписей для оси X
        val xValueFormatter = getXValueFormatter(data)

        //начинаем формировать данные для графика
        val lineValues = getLinesValue(data) //список значений для каждой линии
        val maxXValues = data.values.size

        //настраиваем линии
        val dataSets = getDataSets(data, lineValues)

        //формируем итоговые данные для отображения
        val lineData = LineData(dataSets)

        //вычисляем масштаб такой, чтобы примерно по оси X было 25 точек
        val zoomScale = max(DEFAULT_ZOOM_SCALE, (maxXValues / APPROXIMATE_QUANTITY_POINT).toFloat())

        return ChartGraphicDto(data.chart, xValueFormatter, lineData, zoomScale)
    }

    /**
     * Получить данные по линиям из графика.
     * @param data данные графика, полученные из базы.
     * @return мап, ключем является порядковый номер линии, значением - массив точек для графика.
     */
    private fun getLinesValue(data: ChartAllDataViewed): MutableMap<Int, ArrayList<Entry>> {
        val result: MutableMap<Int, ArrayList<Entry>> = HashMap()
        //заполняем значения для линий
        for (ind in data.values.indices) {
            val hV = data.values[ind]

            //для каждой линии собираем значения
            for (lineInd in hV.verticalValues.indices) {
                //получаем список значений на линии и добавляем новое значение
                var arrayList = if (result.containsKey(lineInd)) {
                    result[lineInd]
                }
                else {
                    ArrayList()
                }
                if (arrayList == null) {
                    arrayList = ArrayList()
                }

                val yValue: Float? = hV.verticalValues[lineInd]?.value?.toFloat()
                yValue?.let { arrayList.add(Entry(ind.toFloat(), it)) }

                result[lineInd] = arrayList
            }
        }
        return result
    }

    /**
     * Сформировать наборы данных по линиям и значениям.
     * @param data данные графика, полученные из базы.
     * @param lineValues мап с данными по линии, ключем является порядковый номер линии,
     * значением - массив точек для графика.
     */
    private fun getDataSets(data: ChartAllDataViewed, lineValues: MutableMap<Int, ArrayList<Entry>>): ArrayList<ILineDataSet> {
        val result: ArrayList<ILineDataSet> = ArrayList()
        for (lineInd in data.lines.indices) {
            val lineDataSet = LineDataSet(lineValues[lineInd], data.lines[lineInd].name)
            lineDataSet.lineWidth = LINE_WIDTH //толщина линии
            lineDataSet.circleRadius = LINE_POINT_SIZE //размер точки значения на линии
            lineDataSet.valueTextSize = LINE_POINT_VALUE_SIZE //рамер значения на самой линии
            lineDataSet.color = ColorConvert.hexToColor(data.lines[lineInd].color)
            lineDataSet.setCircleColor(ColorConvert.hexToColor(data.lines[lineInd].color))
            result.add(lineDataSet)
        }
        return result
    }

    /**
     * Преобразовать подпись по оси X к нужному виду с учетом формата отображения даты.
     * @param dateFormat формат отображения даты при [Chart.xValueType] = [DB.ValueTypes.DATE].
     * @param value исходнаяподпись значения.
     * @return значение в виде преобразованной даты, либо исходное значение.
     */
    private fun convertXAxisValue(dateFormat: String?, value: String): String {
        dateFormat?.let { pattern ->
            try {
                val mainDateFormat = SimpleDateFormat(App.DATE_FORMAT, Locale.getDefault())
                val newDateFormat = SimpleDateFormat(pattern, Locale.getDefault())
                val date = mainDateFormat.parse(value)
                date?.let { return newDateFormat.format(it) }
            } catch (e: Exception) {
                return value
            }
        }

        return value
    }

    /**
     * Сформировать форматтер для отображения значений по оси X.
     * @param data данные графика, полученные из базы.
     * @return подготовленные данные для отображения по оси X.
     */
    private fun getXValueFormatter(data: ChartAllDataViewed): XValueFormatter {
        //сформируем список подписей для оси X
        var dateFormat: String? = null
        if (data.chart.xValueType == DB.ValueTypes.DATE) {
            data.chart.xValueDateFormat?.let { dateFormat = it.dateFormat }
        }
        return XValueFormatter(data.values.map { hV ->
            convertXAxisValue(dateFormat, hV.horizontalValue.value)
        })
    }
}