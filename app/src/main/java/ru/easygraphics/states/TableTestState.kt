package ru.easygraphics.states

import io.github.ekiryushin.scrolltableview.cell.RowCell

sealed class TableTestState : BaseState {
    data class LoadData(
        val header: RowCell,
        val data: MutableList<RowCell>,
        val graphName: String
    ) : TableTestState()

    data class SavedData(val charId: Long?) : TableState()
}
