package ru.easygraphics.states

import com.github.ekiryushin.scrolltableview.cell.RowCell

sealed class TableTestState : BaseState {
    data class Success(
        val header: RowCell,
        val data: MutableList<RowCell>,
        val graphName: String
    ) : TableTestState()
}