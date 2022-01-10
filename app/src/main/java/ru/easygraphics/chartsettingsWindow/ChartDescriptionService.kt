package ru.easygraphics.chartsettingsWindow

import androidx.room.Transaction
import ru.easygraphics.data.db.entities.Chart
import ru.easygraphics.data.db.entities.ChartLine
import ru.easygraphics.data.db.repositories.DataRepository

/** Сервис для обработки данных по графику перед использованием репозитория */
class ChartDescriptionService(private val repository: DataRepository) {

    /**
     * Сохранить данные по графику в базу.
     * @param chart [Chart] данны по графику
     * @param lines [List]<[ChartLine]> данны по линиям графика
     * @param linesDelete [List]<[Long]> Список идентификаторов линий, которые необходимо удалить
     * @return пара, на первом месте которой сохраненный график,
     * а на втором - список сохраненных линий на графике
     */
    @Transaction
    suspend fun saveDataToDB(
        chart: Chart,
        lines: List<ChartLine>,
        linesDelete: List<Long>?): Pair<Chart, List<ChartLine>> {

        //сперва удаляем линии
        linesDelete?.let { repository.deleteLines(it) }

        //сохраняем график
        val chartId = repository.saveChart(chart)
        val chartSaved = Chart(chartId, chart)

        //сохраняем линии
        lines.forEach { line -> line.chartId = chartId }
        repository.saveLines(lines)
        //нет возможности сразу получить обновленные сущности, поэтому приходится их отдельно доставать
        val linesSaved = repository.getLines(chartId)

        return Pair(chartSaved, linesSaved)
    }

    /**
     * Получить данные по графику.
     * @param chartId [Long] идентифкатор графика
     * @return пара, на первом месте которой график, а на втором - список линий на графике
     */
    suspend fun getData(chartId: Long): Pair<Chart, List<ChartLine>> {
        val chart = repository.getChart(chartId = chartId)
        val lines = repository.getLines(chartId = chartId)
        return Pair(chart, lines)
    }
}