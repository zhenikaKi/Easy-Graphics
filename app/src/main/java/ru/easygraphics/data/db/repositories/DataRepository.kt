package ru.easygraphics.data.db.repositories

import ru.easygraphics.data.db.entities.Chart
import ru.easygraphics.data.db.entities.ChartAllDataViewed

/** Интерфейс работы с данными графиков */
interface DataRepository {

    /**
     * Получить все данные по графику в виде, удобном для редактирования.
     * @param chartId идентификатор конкретного графика
     */
    suspend fun getGraphicData(chartId: Long): ChartAllDataViewed
    suspend fun saveChartDescription(chart:Chart,list_y_lines:List<Pair<String,Int>>):Long
    /** Сохранить основные данные по графику */
    suspend fun saveChart(chart: Chart)
}