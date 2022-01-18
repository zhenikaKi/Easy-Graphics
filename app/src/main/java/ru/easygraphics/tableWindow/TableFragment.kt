package ru.easygraphics.tableWindow

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent.getKoin
import ru.easygraphics.R
import ru.easygraphics.arguments
import ru.easygraphics.baseobjects.BaseFragment
import ru.easygraphics.data.db.entities.ChartAllData
import ru.easygraphics.data.domain.TableLineData
import ru.easygraphics.databinding.FragmentTableBinding
import ru.easygraphics.helpers.consts.App
import ru.easygraphics.helpers.consts.Scopes
import ru.easygraphics.parseToListOfTableLineData
import ru.easygraphics.states.BaseState
import ru.easygraphics.states.TableState
import ru.easygraphics.tableWindow.adapter.TableAdapterV
import ru.easygraphics.toast

class TableFragment : BaseFragment<FragmentTableBinding>(FragmentTableBinding::inflate),
    TableAdapterV.Delegate {

    companion object {
        private const val ARG_CHART_ID = "argument_chart_id"
        private const val ARG_CHART_NAME = "argument_chart_name"

        fun newInstance(chartId: Long, chartName: String): Fragment =
            TableFragment()
                .arguments(
                    ARG_CHART_ID to chartId,
                    ARG_CHART_NAME to chartName
                )
    }

    private val chartId: Long? by lazy {
        arguments?.getLong(ARG_CHART_ID, 1)
    }

    private val chartName: String by lazy {
        arguments?.getString(ARG_CHART_NAME).orEmpty()
    }

    private val scope = getKoin().createScope<TableFragment>()

    private val tableViewModel: TableViewModel =
        scope.get(qualifier = named(Scopes.TABLE_VIEW_MODEL))

    //private val router: Router = scope.get(qualifier = named(Scopes.ROUTER))

    private val tableAdapterV: TableAdapterV = TableAdapterV(delegate = this)
    //private val tableAdapter: TableAdapter = TableAdapter(delegate = this)

    private var tableLineList = ArrayList<TableLineData>()


    /*override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            chartId?.let { tableViewModel.fetchTableRows(chartId = it) }
        }
    }*/

    /*override fun initAfterCreate() {
        //super.initAfterCreate()
        chartId?.let { model.fetchTableRows(chartId = it) }
    }*/

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle(R.string.title_table)

        binding.table.adapter = tableAdapterV

        binding.graphName.text = "Graph name"
        tableViewModel.getLiveData().observe(viewLifecycleOwner, { renderData(it) })

        chartId?.let { tableViewModel.fetchTableRows(it) }

        /*viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            tableViewModel.error.collect {
                this@TableFragment.toast(it)
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            tableViewModel.rowData.collect { rowData ->
                adapter.setData(rowData)
            }
        }*/
    }

    private fun renderData(state: BaseState) {
        when (state) {
            is BaseState.Loading -> { }
            is TableState.Success -> showTable(state.data)

            is BaseState.ErrorState -> Log.d(App.LOG_TAG, state.text)
        }
    }

    private fun showTable(data: ChartAllData) {
        //tableAdapter.setData(data.parseToListOfTableLineData())
        //tableAdapter.submitList(data.parseToListOfTableLineData())
        tableLineList = data.parseToListOfTableLineData() as ArrayList<TableLineData>
        tableAdapterV.setData(tableLineList)
    }

    override fun onRowSelectedV(tableLineData: TableLineData) {
        this.toast(tableLineData.lineName)
        //tableAdapterV.removeItem()
    }

    /*override fun onDeleteSelected(position: Int) {
        this.toast("$position to delete")
        val item = tableLineList[position]
        val list = ArrayList<TableLineData>(tableLineList)
        list.remove(item)
        tableLineList = list
        tableAdapterV.setData(list)
    }*/

    override fun onDeleteSelectedV(position: Int) {
        this.toast("$position to delete")
        tableAdapterV.removeItem(position = position)
    }
}