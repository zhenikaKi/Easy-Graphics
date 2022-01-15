package ru.easygraphics.data.db.repositories

import io.github.ekiryushin.scrolltableview.cell.RowCell
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

    /** Удалить значения строки в таблице */
    suspend fun deleteRows(xValuesId: List<Long>)

    /** Обновить значения строки в таблице */
    suspend fun updateRowCells(horizontalValue: HorizontalValue?, verticalValues: List<VerticalValue>?)

    /** Сохранить изменения значения строки в таблице */
    suspend fun insertHorizontalValue(horizontalValue: HorizontalValue?): Long?

    /** Сохранить изменения значения строки в таблице */
    suspend fun insertVerticalValues(verticalValues: List<VerticalValue>)
}