package ru.easygraphics.graphicwindow

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.os.bundleOf
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent.getKoin
import ru.easygraphics.baseobjects.BaseFragment
import ru.easygraphics.data.db.entities.ChartAllDataViewed
import ru.easygraphics.databinding.FragmentGraphicBinding
import ru.easygraphics.helpers.ColorConvert
import ru.easygraphics.helpers.XValueFormatter
import ru.easygraphics.helpers.consts.App
import ru.easygraphics.helpers.consts.DB
import ru.easygraphics.helpers.consts.Scopes
import ru.easygraphics.states.BaseState
import ru.easygraphics.states.GraphicState

class GraphicFragment :
    BaseFragment<FragmentGraphicBinding>(FragmentGraphicBinding::inflate)
{
    private val scope = getKoin().createScope<GraphicFragment>()

    private val model: GraphicViewModel = scope.get(qualifier = named(Scopes.GRAPHIC_VIEW_MODEL))

    //id графика для отображения
    private val chartId by lazy { arguments?.getLong(DB.CHART_ID) }

    companion object {
        fun newInstance(chartId: Long): Fragment = GraphicFragment()
            //передаем во фрагмент id графика
            .also {
                it.arguments = bundleOf(DB.CHART_ID to chartId)
            }
    }

    override fun initAfterCreate() {
        //задаем настройки графика
        with(binding.lineChart) {
            setDrawGridBackground(false) //фон графика сделать прозрачным
            description.isEnabled = false //отключить описание графика
            setDrawBorders(false) //скрыть рамку графика

            axisLeft.isEnabled = true //включить подпись слева
            axisLeft.setDrawAxisLine(true) //показывать линию оси Y слева
            axisLeft.setDrawGridLines(true) //показывать горизонтальны линии по значениям оси Y слева
            axisLeft.textSize = 10f //размер текста

            axisRight.isEnabled = false //отключить подпись справа
            axisRight.setDrawAxisLine(false) //отключить линию оси Y справа
            axisRight.setDrawGridLines(false) //отключить горизонтальны линии по значениям оси Y справа

            xAxis.setDrawAxisLine(true) //показывать линию оси X
            xAxis.setDrawGridLines(false) //отключить вертикальные линии по значениям оси X
            xAxis.position = XAxis.XAxisPosition.BOTTOM //подпись по оси X расположить снизу
            xAxis.textSize = 10f //размер текста

            setTouchEnabled(true) //включить обработку нажатий пальцами (масштабирование, перемещение)
            isDragEnabled = true //включить перемещение по диаграмме (смещение графиков пальцем)
            setScaleEnabled(true) //включить масштабирование двумя пальцами
            setPinchZoom(true) //включить одновременное изменение масштаба по X и Y

            //настройка легенды
            with(legend) {
                verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
                orientation = Legend.LegendOrientation.HORIZONTAL
                setDrawInside(false) //отключить расположение легенды внутри графика
                form = Legend.LegendForm.CIRCLE //задать форму цветовых меток
                formSize = 9f //размер цветовой метки
                textSize = 12f //размер текста легенды
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //связываем fragment с viewModel
        model.getLiveData().observe(viewLifecycleOwner, { renderData(it) })

        //запускаем процесс получения данных по графику
        chartId?.let { model.loadGraphicData(it) }
    }

    //на экране с графиком меню сверху справа не нужно
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    }

    //обработка состояний
    private fun renderData(state: BaseState) {
        when (state) {
            //начало процесса загрузки
            is BaseState.Loading -> { }

            //полученные данные по графику
            is GraphicState.Success -> showGraphic(state.data)

            //какая-то ошибка
            is BaseState.ErrorState -> Log.d(App.LOG_TAG, state.text)
        }
    }

    //отобразить график по полученным данным
    private fun showGraphic(data: ChartAllDataViewed) {
        binding.lineChart.resetTracking()

        //сформируем список подписей для оси X
        //todo по хорошему бы весь код ниже вынести в GraphicViewModel
        binding.lineChart.xAxis.valueFormatter = XValueFormatter(data.values.map { hV -> hV.horizontalValue.value })

        //начинаем формировать данные для графика
        val dataSets: ArrayList<ILineDataSet> = ArrayList()
        val lineValues: MutableMap<Int, ArrayList<Entry>> = HashMap() //список значений для каждойлинии
        //заполняем значения для линий
        for (ind in data.values.indices) {
            val hV = data.values[ind]

            //для каждой линии собираем значения
            for (lineInd in hV.verticalValues.indices) {
                //получаем список значений на линии и добавляем новое значение
                var arrayList = if (lineValues.containsKey(lineInd)) {
                    lineValues[lineInd]
                }
                else {
                    ArrayList()
                }

                if (arrayList == null) {
                    arrayList = ArrayList()
                }

                /*todo библиотека вроде не умеет обрабатывать пустые значения, поэтому 0.
                Но надо еще покопать в сторону ValueFormatter.
                 */

                val yValue: Float = if (hV.verticalValues[lineInd] == null || hV.verticalValues[lineInd]?.value == null) {
                    0f
                }
                else {
                    hV.verticalValues[lineInd]?.value?.toFloat()?: 0f
                }

                arrayList.add(Entry(ind.toFloat(), yValue))
                lineValues[lineInd] = arrayList
            }
        }

        //настраиваем линии
        for (lineInd in data.lines.indices) {
            val lineDataSet: LineDataSet = LineDataSet(lineValues[lineInd], data.lines[lineInd].name)
            lineDataSet.lineWidth = 2.5f //толщина линии
            lineDataSet.circleRadius = 4f //размер точки значения на линии
            lineDataSet.valueTextSize = 10f //рамер значения на самой линии
            lineDataSet.color = ColorConvert.hexToColor(data.lines[lineInd].color)
            lineDataSet.setCircleColor(ColorConvert.hexToColor(data.lines[lineInd].color))
            dataSets.add(lineDataSet)
        }

        val lineData = LineData(dataSets)
        binding.lineChart.data = lineData
        binding.lineChart.invalidate()
    }
}