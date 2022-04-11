package ru.easygraphics.states

import ru.easygraphics.tabletest.data.Cell
import ru.easygraphics.tabletest.data.RowHeaderCell

sealed class TableTestState : BaseState {
    data class LoadData(
        val columnHeaders: List<Cell>,
        val rowHeaders: List<RowHeaderCell>,
        val cells: List<List<Cell>>,
        val graphName: String?
    ) : TableTestState()

    data class SavedData(val charId: Long?) : TableState()
}
