package ru.easygraphics.states

import ru.easygraphics.data.db.entities.ChartAllDataViewed

sealed class GraphicState: BaseState {
    data class Success(val data: ChartAllDataViewed): GraphicState()
}
