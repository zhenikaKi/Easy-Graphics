package ru.easygraphics.tabletest

import android.os.Bundle
import android.util.Log
import android.view.View
import com.github.ekiryushin.scrolltableview.cell.Cell
import com.github.ekiryushin.scrolltableview.cell.CellView
import com.github.ekiryushin.scrolltableview.cell.DataStatus
import com.github.ekiryushin.scrolltableview.cell.RowCell
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent.getKoin
import ru.easygraphics.baseobjects.BaseFragment
import ru.easygraphics.databinding.FragmentTestTableBinding
import ru.easygraphics.helpers.consts.App
import ru.easygraphics.helpers.consts.Scopes
import ru.easygraphics.states.BaseState
import ru.easygraphics.states.TableState

class TableTestFragment :
    BaseFragment<FragmentTestTableBinding>(FragmentTestTableBinding::inflate) {

    private val scope = getKoin().createScope<TableTestFragment>()
    private val model: TableTestViewModel = scope.get(qualifier = named(Scopes.TABLE_TEST_VIEW_MODEL))

    private var countColumns = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model.getLiveData().observe(viewLifecycleOwner, { renderData(it) })
        model.loadTableData(chartId = 1)

        //добавление новой строки
        binding.buttonAdd.setOnClickListener {
            //сформируем пустую строку
            val columns: MutableList<Cell> = mutableListOf()
            columns.add(Cell(viewed = CellView.EDIT_STRING)) //столбец значения по оси X
            //значения по оси Y
            for (ind in 1 .. countColumns) {
                columns.add(Cell(viewed = CellView.EDIT_NUMBER))
            }
            binding.tableDataBlock.addRowData(RowCell(columns))
        }

        //вывод в лог данных по таблице
        binding.buttonLog.setOnClickListener {
            val data = binding.tableDataBlock.getData()
            //выведем новые, удаленные строки или строки, где поменялись значения
            val editedDate = data?.filter { row ->
                row.status == DataStatus.ADD || row.status == DataStatus.DELETE
                        || row.columns.any { column -> column.status == DataStatus.EDIT }
            }
            Log.d(App.LOG_TAG, editedDate.toString())
        }
    }

    private fun renderData(state: BaseState) {
        when (state) {
            //полученные данные по графику
            is TableState.Success -> showTableData(state.header, state.data)
        }
    }

    private fun showTableData(header: RowCell, data: MutableList<RowCell>) {
        countColumns = header.columns.size
        binding.tableDataBlock.setHeaders(header)
        binding.tableDataBlock.setData(data)
        binding.tableDataBlock.setEnabledIconDelete(true)
        binding.tableDataBlock.setCountFixColumn(1)
        binding.tableDataBlock.showTable()
    }
}