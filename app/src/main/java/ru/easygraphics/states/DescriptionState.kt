package ru.easygraphics.states

import ru.easygraphics.data.db.entities.Chart
import ru.easygraphics.data.db.entities.ChartLine

sealed class DescriptionState:BaseState {
    data class Success(val chart_id:Long): DescriptionState()
    data class LoadData(val chart: Chart, val lines: List<ChartLine>): DescriptionState()
}