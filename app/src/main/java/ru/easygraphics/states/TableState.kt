package ru.easygraphics.states

import com.github.ekiryushin.scrolltableview.cell.RowCell

sealed class TableState: BaseState {
    data class Success(val header: RowCell, val data: MutableList<RowCell>): TableState()
}
