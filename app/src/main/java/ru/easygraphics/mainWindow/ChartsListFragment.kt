package ru.easygraphics.mainWindow

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent.getKoin
import ru.easygraphics.tabletest.TableTestScreen
import ru.easygraphics.baseobjects.BaseFragment
import ru.easygraphics.data.db.AppDB
import ru.easygraphics.databinding.FragmentChartsListBinding
import ru.easygraphics.helpers.consts.App
import ru.easygraphics.helpers.consts.Scopes
import ru.easygraphics.tableWindow.TableScreen

class ChartsListFragment :
    BaseFragment<FragmentChartsListBinding>(FragmentChartsListBinding::inflate) {

    private val scope = getKoin().createScope<ChartsListFragment>()
    private val router: Router = scope.get(qualifier = named(Scopes.ROUTER))

    companion object {
        fun newInstance(): Fragment = ChartsListFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        printDataFromDBForTest()
        binding.floatingActionButton.setOnClickListener {
            //router.navigateTo(ChartDescriptionScreen(null))
            router.navigateTo(GraphicScreen(1)) //для теста
            //router.navigateTo(ChartDescriptionScreen(-1))
            //router.navigateTo(GraphicScreen(1)) //для теста
            //router.navigateTo(TableScreen(1, "Chart Name")) //для теста
            router.navigateTo(TableTestScreen()) //для теста
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