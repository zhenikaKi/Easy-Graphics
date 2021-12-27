package ru.easygraphics.data.db.repositories

import ru.easygraphics.data.db.entities.ChartAllData
import ru.easygraphics.states.TableState

interface TableRowRepository {

    suspend fun getAllDataOnChartId(chartId: Long): TableState<ChartAllData>
}