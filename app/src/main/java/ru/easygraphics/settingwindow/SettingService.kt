package ru.easygraphics.settingwindow

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.room.Transaction
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import ru.easygraphics.R
import ru.easygraphics.data.db.converts.DateTypesConvert
import ru.easygraphics.data.db.converts.ValueTypesConvert
import ru.easygraphics.data.db.entities.*
import ru.easygraphics.data.db.repositories.DataRepository
import ru.easygraphics.data.dto.ChartDto
import ru.easygraphics.data.dto.FileDto
import ru.easygraphics.data.dto.LineDto
import ru.easygraphics.helpers.consts.App
import ru.easygraphics.helpers.consts.DB
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*

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
     * Выполнить экспорт данных.
     * @param contentResolver для выполнения запросов от активности к контент-провайдеру.
     */
    suspend fun exportGraphics(contentResolver: ContentResolver): String {
        //формируем данные по всем графикам
        val data: FileDto = generateFileDto()
        val jsonData: String = Gson().toJson(data)

        //сохраняем файл
        val fileName = "${data.date}.json"
        saveDataInFile(contentResolver, fileName, jsonData)

        return fileName
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

    /** Сформировать данные для экспорта. */
    private suspend fun generateFileDto(): FileDto {
        //получаем все данные из базы
        val graphics: List<ChartAllData> = repository.getAllDataOnAllCharts()
        //конвертируем данные к структуре json
        val chartsDto: List<ChartDto> = graphics.map { chartAllDataToChartDto(it) }

        val currentDate =
            SimpleDateFormat(App.DATE_FORMAT, Locale.getDefault()).format(System.currentTimeMillis())
        return FileDto(
            version = DB.VERSION,
            date = currentDate,
            charts = chartsDto
        )
    }

    /**
     * Преобразовать сущнось графика к dto.
     * @param chartAllData исходная сущность линии.
     * @return dto графика для экспорта
     */
    private fun chartAllDataToChartDto(chartAllData: ChartAllData): ChartDto {
        //значения по оси X каждого графика
        val xValues = chartAllData.xValues.map { horizontalValue -> horizontalValue.value }
        //список линий со значениями каждого графика
        val chartLines: List<LineDto> = chartAllData.lines.map { chartLineDataToLineDto(it) }

        return ChartDto(
            title = chartAllData.chart.name,
            countDecimal = chartAllData.chart.countDecimal,
            xValueType = chartAllData.chart.xValueType.value,
            xValueDateFormat = chartAllData.chart.xValueDateFormat?.value,
            xName = chartAllData.chart.xName,
            yName = chartAllData.chart.yName,
            xValues = xValues,
            lines = chartLines
        )
    }

    /**
     * Преобразовать сущнось линии к dto.
     * @param chartLineData исходная сущность линии.
     * @return dto линии для экспорта
     */
    private fun chartLineDataToLineDto(chartLineData: ChartLineData): LineDto {
        val yValues = chartLineData.yValues.map { verticalValue -> verticalValue.value }
        return LineDto (
            title = chartLineData.chartLine.name,
            color = chartLineData.chartLine.color,
            yValues = yValues
        )
    }

    /**
     * Выполнить сохранение данных в файл для разных версий Android.
     * @param contentResolver для выполнения запросов от активности к контент-провайдеру.
     * @param fileName имя файла для сохранения.
     * @param jsonData содержимое файла.
     */
    private fun saveDataInFile(contentResolver: ContentResolver, fileName: String, jsonData: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            }
            val uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            uri?.let {
                val outputStream = contentResolver.openOutputStream(it)
                outputStream?.write(jsonData.toByteArray())
            }
        }
        else {
            val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val fileOutputStream = FileOutputStream(File(downloadDir, fileName))
            fileOutputStream.write(jsonData.toByteArray())
        }
    }
}