package ru.easygraphics.tabletest

import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import io.github.ekiryushin.scrolltableview.cell.Cell
import io.github.ekiryushin.scrolltableview.cell.CellView
import io.github.ekiryushin.scrolltableview.cell.DataStatus
import io.github.ekiryushin.scrolltableview.cell.RowCell
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent.getKoin
import ru.easygraphics.R
import ru.easygraphics.baseobjects.BaseFragment
import ru.easygraphics.databinding.FragmentTestTableBinding
import ru.easygraphics.helpers.consts.App
import ru.easygraphics.helpers.consts.DB
import ru.easygraphics.helpers.consts.Scopes
import ru.easygraphics.states.BaseState
import ru.easygraphics.states.TableTestState
import ru.easygraphics.toast

class TableTestFragment :
    BaseFragment<FragmentTestTableBinding>(FragmentTestTableBinding::inflate) {

    private val scope = getKoin().createScope<TableTestFragment>()
    private val viewModel: TableTestViewModel =
        scope.get(qualifier = named(Scopes.TABLE_TEST_VIEW_MODEL))

    private var countColumns = 0

    //id графика для отображения
    private val chartId by lazy { arguments?.getLong(DB.CHART_ID) }

    companion object {
        fun newInstance(chartId: Long): Fragment = TableTestFragment()
            //передаем во фрагмент id графика
            .also {
                it.arguments = bundleOf(DB.CHART_ID to chartId)
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getLiveData().observe(viewLifecycleOwner, { renderData(it) })
        loadTableDataById()

        //добавление новой строки
        binding.buttonAdd.setOnClickListener {
            //сформируем пустую строку
            val columns: MutableList<Cell> = mutableListOf()
            columns.add(Cell(viewed = viewModel.getXAxisViewed())) //столбец значения по оси X
            //значения по оси Y
            for (ind in 1 until countColumns) {
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
            //в строках, где поменялись значения, оставим только измененные значения
            editedDate?.filter { row -> row.status == DataStatus.NORMAL }
                ?.forEach { row ->
                    row.columns = row.columns.filter { column -> column.status == DataStatus.EDIT }
                }
            Log.d(App.LOG_TAG, editedDate.toString())
        }
    }

    private fun renderData(state: BaseState) {
        when (state) {
            is BaseState.Loading -> { }
            is TableTestState.Success -> showTableData(state.header, state.data, state.graphName)
            is BaseState.ErrorState -> Log.d(App.LOG_TAG, state.text)
        }
    }

    private fun showTableData(header: RowCell, data: MutableList<RowCell>, graphicName: String) {
        countColumns = header.columns.size
        with(binding) {
            graphName.text = graphicName
            tableDataBlock.setHeader(header)
            tableDataBlock.setData(data)
            tableDataBlock.setEnabledIconDelete(true)
            tableDataBlock.setCountFixColumn(1)
            tableDataBlock.showTable()
        }

    }

    override fun saveData() {
        view?.let { view ->
            registerForContextMenu(view)
            view.showContextMenu()
        }
    }

    override fun onCreateContextMenu(
        contextMenu: ContextMenu,
        view: View,
        contextMenuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(contextMenu, view, contextMenuInfo)
        requireActivity().menuInflater.inflate(R.menu.menu_save_confirmation, contextMenu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_save -> {
                this@TableTestFragment.toast("Сохранил")
                loadTableDataById()
            }
        }
        return true
    }

    private fun loadTableDataById() = chartId?.let { viewModel.loadTableData(chartId = it) }
}