package ru.easygraphics.states

import ru.easygraphics.data.db.entities.Chart
import ru.easygraphics.data.db.entities.ChartLine

sealed class DescriptionState: BaseState {
    data class Saved(val chart: Chart, val lines: List<ChartLine>): DescriptionState()
    data class LoadData(val chart: Chart, val lines: List<ChartLine>): DescriptionState()
}