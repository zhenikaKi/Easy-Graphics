package ru.easygraphics.settingwindow

import android.content.Context
import android.util.Log
import androidx.room.Transaction
import com.google.gson.GsonBuilder
import org.json.JSONArray
import org.json.JSONObject
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
import ru.easygraphics.helpers.consts.App
import ru.easygraphics.helpers.consts.DB
import java.io.File
import java.io.FileReader
import java.lang.Exception
import java.lang.RuntimeException
import java.lang.StringBuilder
import kotlin.math.log

/** Сервис для обработки данных в настройках перед использованием репозитория */
class SettingService(private val repository: DataRepository) {

    /** Сформировать список пунктов настроек */
    fun getItems(context: Context): List<SettingItemType> {
        val externalFilesDir = context.getExternalFilesDir(null)?.absolutePath
        val filePath = externalFilesDir?.let { "$it/${App.FILE_IMPORT_NAME}" } ?: ""

        return listOf(
            //экспорт данных
            /* todo нужно позже включить SettingMainItem(
                title = context.getString(R.string.setting_export_title),
                description = context.getString(R.string.setting_export_description),
                itemType = SettingItemType.EXPORT_TYPE
            ),*/

            //импорт данных
            SettingMainItem(
                title = context.getString(R.string.setting_import_title),
                description = String.format(context.getString(R.string.setting_import_description), filePath),
                itemType = SettingItemType.IMPORT_TYPE
            )
        )
    }

    /**
     * Выполнить импорт данных.
     */
    suspend fun importGraphics(context: Context){
        val data = getFileData(context)
        //val jsonData = getJsonObject(data)
        //importProcess(jsonData)
        importProcess(data)
    }

    /**
     * Прочитать содержимое файла для импорта.
     * @return содержимое файла.
     */
    private fun getFileData(context: Context): String {
        val externalFilesDir = context.getExternalFilesDir(null)
        val file = File(externalFilesDir, App.FILE_IMPORT_NAME)

        if (file.exists()) {
            val bufferedReader = FileReader(file)
            val stringBuilder = StringBuilder()
            bufferedReader.readLines().forEach { stringBuilder.append(it) }
            return stringBuilder.toString()
        }
        else {
            throw RuntimeException("Отсутствует файл: ${file.absolutePath}")
        }
    }

    /**
     * Процесс загрузки данных.
     * @param jsonData данные по графикам для импорта.
     */
    private suspend fun importProcess(json: String) {
        //конвертируем json в dto-объект
        val fileDto: FileDto = GsonBuilder().create().fromJson(json, FileDto::class.java)

        //обходим все графики
        fileDto.charts.forEach { chartDto -> importChart(chartDto) }

        Log.d(App.LOG_TAG, fileDto.toString())
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