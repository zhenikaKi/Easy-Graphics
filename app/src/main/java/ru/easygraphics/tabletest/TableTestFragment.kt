package ru.easygraphics.tabletest

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.evrencoskun.tableview.listener.SimpleTableViewListener
import com.github.terrakok.cicerone.Router
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent.getKoin
import ru.easygraphics.R
import ru.easygraphics.baseobjects.BaseFragment
import ru.easygraphics.databinding.FragmentTestTableBinding
import ru.easygraphics.extensions.toast
import ru.easygraphics.helpers.AlertDialogs
import ru.easygraphics.helpers.consts.App
import ru.easygraphics.helpers.consts.DB
import ru.easygraphics.helpers.consts.Scopes
import ru.easygraphics.states.BaseState
import ru.easygraphics.states.TableTestState
import ru.easygraphics.tabletest.data.Cell
import ru.easygraphics.tabletest.data.DataStatus
import ru.easygraphics.tabletest.data.RowHeaderCell
import ru.easygraphics.visibleOrGone

class TableTestFragment :
    BaseFragment<FragmentTestTableBinding>(FragmentTestTableBinding::inflate) {

    private val scope = getKoin().createScope<TableTestFragment>()
    private val viewModel: TableTestViewModel =
        scope.get(qualifier = named(Scopes.TABLE_TEST_VIEW_MODEL))
    private val router: Router = scope.get(qualifier = named(Scopes.ROUTER))

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
        setTitle(R.string.title_table)
        viewModel.getLiveData().observe(viewLifecycleOwner) { renderData(it) }
        loadTableDataById()

        //добавление новой строки
        binding.addLineButton.setOnClickListener {
            val addNewRow = viewModel.addNewRow(getAdapter())
            openEditDialog(addNewRow, true)
            binding.tableDataBlock.scrollToRowPosition(addNewRow)
        }
    }

    private fun renderData(state: BaseState) {
        when (state) {
            is BaseState.Loading ->  binding.progressBar.visibleOrGone(true)

            is TableTestState.LoadData -> {
                showTableData(state.columnHeaders, state.rowHeaders, state.cells, state.graphName)
                binding.progressBar.visibleOrGone(false)

            }
            is TableTestState.SavedData -> {
                loadTableDataById()
            }
            is BaseState.ErrorState -> Log.d(App.LOG_TAG, state.text)
        }
    }

    private fun showTableData(columnHeaders: List<Cell>,
                              rowHeaders: List<RowHeaderCell>,
                              cells: List<List<Cell>>,
                              graphicName: String) {
        countColumns = columnHeaders.size
        binding.graphName.text = graphicName
        val tableAdapter = TableTestAdapter(rowEventListener)
        binding.tableDataBlock.setAdapter(tableAdapter)
        tableAdapter.setAllItems(columnHeaders, rowHeaders, cells)

        binding.tableDataBlock.tableViewListener = cellListener
        binding.tableDataBlock.isIgnoreSelectionColors = true
    }

    override fun saveData() {
        val data = getAdapter().getAllCell()
        val linesId: List<Long?> = getAdapter().getHeaders().map { column -> column.id }

        chartId?.let {
            viewModel.updateTableData(chartId, data, linesId)
        }
        requireContext().toast(getString(R.string.message_save))
        router.exit()
    }

    private fun loadTableDataById() = chartId?.let { viewModel.loadTableData(chartId = it) }

    /** Обработчик нажатия на ячейки таблицы */
    private val cellListener = object: SimpleTableViewListener() {
        override fun onCellClicked(cellView: RecyclerView.ViewHolder, column: Int, row: Int) {
            openEditDialog(row)
        }
    }

    /** Обработчик событий на строке */
    private val rowEventListener = object: RowEventListener {
        override fun removeRow(rowId: Int) {
            val cell = getAdapter().getRowHeaderItem(rowId)
            cell?.let {
                it.statusBeforeDelete = it.rowStatus
                it.rowStatus = DataStatus.ROW_DELETE
            }
            getAdapter().changeRowHeaderItem(rowId, cell)
        }

        override fun restoreRow(rowId: Int) {
            val cell = getAdapter().getRowHeaderItem(rowId)
            cell?.let {
                it.rowStatus = it.statusBeforeDelete
            }
            getAdapter().changeRowHeaderItem(rowId, cell)
        }

    }

    /** Открыть диалоговое окно редактирования данных по строке */
    private fun openEditDialog(rowId: Int, asNewRow: Boolean = false) {
        //удаленные строки не редактируем
        if (getAdapter().getRowHeaderItem(rowId)?.rowStatus == DataStatus.ROW_DELETE) {
            return
        }

        //получаем данные по строке
        val columns: List<Cell>? =
            getAdapter().getCellRowItems(rowId)
        //данные по заголовку
        val headers: List<Cell> = getAdapter().getHeaders()
        columns?.let {
            AlertDialogs.createEditRowTable(
                requireContext(),
                rowId,
                it,
                headers,
                asNewRow,
                object: TableEditDialogListener{
                    override fun applyDialog(view: LinearLayout) {
                        applyDialog(rowId, view)
                    }

                    override fun cancelDialog(dialog: DialogInterface, rowId: Int, asNewRow: Boolean) {
                        if (asNewRow) {
                            getAdapter().removeRow(rowId)
                        }
                        dialog.cancel()
                    }
                }
            ).show() }
    }

    /** Сохранение значения. */
    private fun applyDialog(rowId: Int, view: LinearLayout) {
        //обходим все поля ввода
        for (ind in 0 until view.childCount) {
            val item = view.getChildAt(ind)
            val editText = item.findViewById<EditText>(R.id.edit_value)
            val value = editText.text.toString()
            val cell = getAdapter().getCellItem(ind, rowId)
            cell?.updateValue(value)
            getAdapter().changeCellItem(ind, rowId, cell)
        }
    }

    /** Получить адаптер таблицы */
    private fun getAdapter() = (binding.tableDataBlock.adapter as TableTestAdapter)
}