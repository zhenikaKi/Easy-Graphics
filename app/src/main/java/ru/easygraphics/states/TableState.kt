package ru.easygraphics.states

import ru.easygraphics.data.db.entities.ChartAllData

sealed class TableState: BaseState {
    data class Success(val data: ChartAllData): TableState()
}