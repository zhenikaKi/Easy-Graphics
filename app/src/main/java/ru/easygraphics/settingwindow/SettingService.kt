package ru.easygraphics.settingwindow

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.room.Transaction
import com.google.gson.GsonBuilder
import ru.easygraphics.R
import ru.easygraphics.data.db.converts.DateTypesConvert
import ru.easygraphics.data.db.converts.ValueTypesConvert
import ru.easygraphics.data.db.entities.Chart
import ru.easygraphics.data.db.entities.ChartLine
import ru.easygraphics.data.db.entities.HorizontalValue
import ru.easygraphics.data.db.entities.VerticalValue
import ru.easygraphics.data.db.repositories.DataRepository
import ru.easygraphics.data.dto.ChartDto
import ru.easygraphics.data.dto.FileDto
import java.io.BufferedReader
import java.io.InputStreamReader

/** Сервис для обработки данных в настройках перед использованием репозитория */
class SettingService(private val repository: DataRepository) {

    /** Сформировать список пунктов настроек */
    fun getItems(context: Context): List<SettingItemType> {
        return listOf(
            //экспорт данных
            SettingMainItem(
                title = context.getString(R.string.setting_export_title),
                description = context.getString(R.string.setting_export_description),
                itemType = SettingItemType.EXPORT_TYPE
            ),

            //импорт данных
            SettingMainItem(
                title = context.getString(R.string.setting_import_title),
                description = context.getString(R.string.setting_import_description),
                itemType = SettingItemType.IMPORT_TYPE
            )
        )
    }

    /**
     * Выполнить импорт данных.
     * @param contentResolver для выполнения запросов от активности к контент-провайдеру.
     * @param uri выбранный файл для импорта.
     */
    suspend fun importGraphics(contentResolver: ContentResolver, uri: Uri){
        val data = getFileData(contentResolver, uri)
        importProcess(data)
    }

    /**
     * Прочитать содержимое файла для импорта.
     * @param contentResolver для выполнения запросов от активности к контент-провайдеру.
     * @param uri выбранный файл для импорта.
     * @return содержимое файла.
     */
    private fun getFileData(contentResolver: ContentResolver, uri: Uri): String {
        val openInputStream = contentResolver.openInputStream(uri)
        openInputStream?.let {
            val bufferedReader = BufferedReader(InputStreamReader(it))
            val stringBuilder = StringBuilder()
            bufferedReader.readLines().forEach { line -> stringBuilder.append(line) }
            return stringBuilder.toString()
        }
        throw RuntimeException("Не получилось прочитать файл")
    }

    /**
     * Процесс загрузки данных.
     * @param json данные по графикам для импорта.
     */
    private suspend fun importProcess(json: String) {
        //конвертируем json в dto-объект
        val fileDto: FileDto = GsonBuilder().create().fromJson(json, FileDto::class.java)

        //обходим все графики
        fileDto.charts.forEach { chartDto -> importChart(chartDto) }
    }

    /**
     * Процесс сохранения графика в базу.
     * @param chartDto данные по графику из файла импорта.
     */
    @Transaction
    private suspend fun importChart(chartDto: ChartDto) {
        //сохраняем график
        val chartId = repository.saveChart(Chart(
            chartId = null,
            name = chartDto.title,
            countDecimal = chartDto.countDecimal,
            xValueType = ValueTypesConvert().valueToEnum(chartDto.xValueType),
            xValueDateFormat = chartDto.xValueDateFormat?.let { DateTypesConvert().valueToEnum(it) },
            xName = chartDto.xName,
            yName = chartDto.yName
        ))

        //сохраняем подписи по X
        var xValues = chartDto.xValues.map {
                value -> HorizontalValue(xValueId = null, chartId = chartId, value = value)
        }
        repository.insertHorizontalValues(xValues)
        xValues = repository.getHorizontalValues(chartId)

        //сохраняем линии и подписи по Y
        chartDto.lines.forEach { lineDto ->
            val lineId =repository.saveLine(ChartLine(
                lineId = null,
                chartId = chartId,
                name = lineDto.title,
                color = lineDto.color))
            val yValues: MutableList<VerticalValue> = mutableListOf()
            for (yInd in lineDto.yValues.indices) {
                yValues.add(VerticalValue(
                    yValueId = null,
                    lineId = lineId,
                    xValueId = xValues[yInd].xValueId!!,
                    value = lineDto.yValues[yInd]))
            }
            repository.insertVerticalValues(yValues)
        }
    }
}