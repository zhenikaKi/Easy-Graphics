package ru.easygraphics.states

import ru.easygraphics.data.db.entities.ChartAndLines

sealed class ChartsListState:BaseState {
    data class Success(val chartsList:List<ChartAndLines>): ChartsListState()
}