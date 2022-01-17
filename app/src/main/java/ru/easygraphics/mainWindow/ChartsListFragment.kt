package ru.easygraphics.mainWindow

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent.getKoin
import ru.easygraphics.R
import ru.easygraphics.baseobjects.BaseFragment
import ru.easygraphics.chartsettingsWindow.ChartDescriptionScreen
import ru.easygraphics.data.db.AppDB
import ru.easygraphics.databinding.FragmentChartsListBinding
import ru.easygraphics.graphicwindow.GraphicScreen
import ru.easygraphics.helpers.consts.App
import ru.easygraphics.helpers.consts.Scopes
import ru.easygraphics.states.BaseState
import ru.easygraphics.states.ChartsListState
import ru.easygraphics.visibleOrGone

class ChartsListFragment :
    BaseFragment<FragmentChartsListBinding>(FragmentChartsListBinding::inflate) {

    companion object {
        const val SHIFT = 10f
        fun newInstance(): Fragment = ChartsListFragment()
    }

    private val scope = getKoin().createScope<ChartsListFragment>()
    private val router: Router = scope.get(qualifier = named(Scopes.ROUTER))
    private val model: ChartsListViewModel =
        scope.get(qualifier = named(Scopes.CHARTS_LIST_VIEW_MODEL))
    private var index: Int? = null
    private var chartIdItem: Long? = null
    private val onChartClickListener = object : ChartsListAdapter.OnChartClickListener {
        override fun onChartClick(chartId: Long) {
            router.navigateTo(GraphicScreen(chartId))
        }
    }
    private val onChartLongClickListener = object : ChartsListAdapter.OnChartLongClickListener {
        @RequiresApi(Build.VERSION_CODES.N)
        override fun onChartLongClick(chartId: Long, pos: Int, view: View) {
            index = pos
            chartIdItem = chartId
            registerForContextMenu(view)
            view.showContextMenu(SHIFT, SHIFT)
        }
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val mi = requireActivity().menuInflater
        mi.inflate(R.menu.context_menu_list_charts, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.edit -> {
                router.navigateTo(ChartDescriptionScreen(chartIdItem!!))
            }
            R.id.delete -> {
                AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.dialog_title))
                    .setMessage(getString(R.string.dialog_message))
                    .setCancelable(false)
                    .setNegativeButton(getString(R.string.dialog_negative_button)) { dialog, _ ->
                        dialog.cancel()
                    }
                    .setPositiveButton(getString(R.string.dialog_positive_button)) { _, _ ->
                        model.deleteChart(chartIdItem!!)
                        adapter.removeItem(index!!)
                    }
                    .create()
                    .show()
            }
        }

        return true
    }

    override fun showButtonBack(visible: Boolean) {
        super.showButtonBack(false)
    }

    private val adapter: ChartsListAdapter =
        ChartsListAdapter(onChartClickListener, onChartLongClickListener)



    private fun renderData(state: BaseState) {
        when (state) {
            //начало процесса загрузки
            is BaseState.Loading -> {
                binding.progressBar.visibleOrGone(true)
            }

            //получены данные
            is ChartsListState.Success -> {
                adapter.setData(state.chartsList)
                binding.progressBar.visibleOrGone(false)
            }

            //какая-то ошибка
            is BaseState.ErrorState -> Log.d(App.LOG_TAG, state.text)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.chartsList.adapter = adapter
        with(binding.chartsList) {
            itemAnimator = DefaultItemAnimator()
            addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        }
        //printDataFromDBForTest()
        model.getLiveData().observe(viewLifecycleOwner, {
            renderData(it)
        })
        model.getChartsList()

        binding.floatingActionButton.setOnClickListener {
            router.navigateTo(ChartDescriptionScreen(null))
        }
    }

    //todo для теста
    private fun printDataFromDBForTest() {
        val db: AppDB = scope.get(qualifier = named(Scopes.DB))
        val coroutineScope = CoroutineScope(
            Dispatchers.IO
                    + SupervisorJob() //дочерние корутины выполняются независимо от ошибок в других корутинах
        )

        coroutineScope.launch {
            val chartData = db.chartAllDataDao().getAllDataOnChartId(1)
            Log.d(App.LOG_TAG, "$chartData")


            /*//получить график
            val chart = db.chartDao().getChart(1)
            Log.d(LOG_TAG, "$chart")

            //получить список подписей по оси X
            val xValues = db.horizontalValueDao().getValues(chartId = chart.chartId)

            //получить список линий
            db.chartLineDao().getLines(chartId = chart.chartId).forEach { chartLine ->
                Log.d(LOG_TAG, "  $chartLine")
                //получить значений по Y у конкретной линии графика
                db.verticalValueDao().getValues(lineId = chartLine.lineId).forEach { yValue ->
                    val xValue = xValues.find { horizontalValue -> horizontalValue.xValueId == yValue.xValueId }
                    xValue?.let {
                        Log.d(LOG_TAG, "    X: ${it.value} (id=${it.xValueId}), " +
                                "Y: ${yValue.value} (id=${yValue.yValueId})")
                    }
                }
            }*/
        }
    }

}