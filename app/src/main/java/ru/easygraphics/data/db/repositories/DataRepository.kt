package ru.easygraphics.data.db.repositories

import ru.easygraphics.data.db.entities.Chart
import ru.easygraphics.data.db.entities.ChartAllDataViewed
import ru.easygraphics.data.db.entities.ChartLine

/** Интерфейс работы с данными графиков */
interface DataRepository {

    /**
     * Получить все данные по графику в виде, удобном для редактирования.
     * @param chartId идентификатор конкретного графика
     */
    suspend fun getGraphicData(chartId: Long): ChartAllDataViewed

    /** Сохранить основные данные по графику */
    suspend fun saveChart(chart: Chart): Long

    suspend fun getChartsList(): List<Pair<Long, String>>
    suspend fun deleteChart(chartId: Long)

    /** Получить конкретный график */
    suspend fun getChart(chartId: Long): Chart

    /** Получить линии графика */
    suspend fun getLines(chartId: Long): List<ChartLine>

    /** Удалить линии графика */
    suspend fun deleteLines(chartLinesId: List<Long>)

    /** Сохранить линии графику */
    suspend fun saveLines(lines: List<ChartLine>)
}