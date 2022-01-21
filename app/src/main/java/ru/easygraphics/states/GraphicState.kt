package ru.easygraphics.states

import ru.easygraphics.graphicwindow.ChartGraphicDto

sealed class GraphicState: BaseState {
    data class Success(val chartGraphicDto: ChartGraphicDto): GraphicState()
}
