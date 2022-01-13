package ru.easygraphics.data.db.repositories

import ru.easygraphics.data.db.entities.*
import ru.easygraphics.states.TableState

/** Интерфейс работы с данными графиков */
interface DataRepository {

    /**
     * Получить все данные по графику в виде, удобном для редактирования.
     * @param chartId идентификатор конкретного графика
     */
    suspend fun getGraphicData(chartId: Long): ChartAllDataViewed

    suspend fun getAllDataOnChartId(chartId: Long): ChartAllData

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

    /** Обновить значения линии в таблице*/
    suspend fun updateVerticalValue(verticalValue: VerticalValue)

    /** Обновить значения линии в таблице*/
    suspend fun updateVerticalValues(verticalValues: List<VerticalValue>)

    /** Вставить значения линии в таблице */
    suspend fun insertVerticalValues(verticalValues: List<VerticalValue>)

    /** Обновить значения X в таблице*/
    suspend fun updateHorizontalValue(horizontalValue: HorizontalValue)

    /** Обновить значения X в таблице*/
    suspend fun updateHorizontalValues(horizontalValues: List<HorizontalValue>)

    /** Удалить значения X в таблице */
    suspend fun deleteHorizontalValue(xValuesId: List<Long>)

    /** Вставить значения X в таблице */
    suspend fun insertHorizontalValue(horizontalValue: HorizontalValue): Long
}