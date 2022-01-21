package ru.easygraphics.graphicwindow

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.components.*
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent.getKoin
import ru.easygraphics.R
import ru.easygraphics.baseobjects.BaseFragment
import ru.easygraphics.data.db.entities.Chart
import ru.easygraphics.databinding.FragmentGraphicBinding
import ru.easygraphics.helpers.consts.App
import ru.easygraphics.helpers.consts.DB
import ru.easygraphics.helpers.consts.Scopes
import ru.easygraphics.states.BaseState
import ru.easygraphics.states.GraphicState
import ru.easygraphics.visibleOrGone

class GraphicFragment :
    BaseFragment<FragmentGraphicBinding>(FragmentGraphicBinding::inflate)
{
    private val scope = getKoin().createScope<GraphicFragment>()

    private val model: GraphicViewModel = scope.get(qualifier = named(Scopes.GRAPHIC_VIEW_MODEL))

    //id графика для отображения
    private val chartId by lazy { arguments?.getLong(DB.CHART_ID) }

    companion object {
        //размер текста значений по осям
        private const val AXIS_FONT_SIZE = 10f
        //максимальное количество точек на графике, при котором показывается само значение над точкой
        private const val MAX_VISIBLE_VALUE = 100
        //размер текста в легенде
        private const val LEGEND_FONT_SIZE = 12f
        //размер индикатора в легенде
        private const val LEGEND_INDICATOR_SIZE = 9f
        //размер в процентах, который занимает легенда относительно графика
        private const val MAX_SIZE_PERCENT = 0.9f
        //толщина нулевой линии на графике
        private const val ZERO_LINE_WIDTH = 2f
        //значение нулевой линии
        private const val ZERO_VALUE = 0f
        //размер пунктира нулевой линии
        private const val ZERO_LINE_LENGTH = 30f
        private const val ZERO_LINE_PHASE = 0f

        fun newInstance(chartId: Long): Fragment = GraphicFragment()
            //передаем во фрагмент id графика
            .also {
                it.arguments = bundleOf(DB.CHART_ID to chartId)
            }
    }

    override fun initAfterCreate() {
        //задаем настройки графика
        setSettingAxes()
        setSettingGraphic()
        setSettingLegend()
        setSettingLimitLines()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle(R.string.title_graphic)

        //связываем fragment с viewModel
        model.getLiveData().observe(viewLifecycleOwner, { renderData(it) })

        //запускаем процесс получения данных по графику
        chartId?.let { model.loadGraphicData(it) }
    }

    /** Задать настройки графика и взаимодействия с ним. */
    private fun setSettingGraphic() {
        with(binding.lineChart) {
            setDrawGridBackground(false) //фон графика сделать прозрачным
            setDrawBorders(false) //скрыть рамку графика
            setTouchEnabled(true) //включить обработку нажатий пальцами (масштабирование, перемещение)
            isDragEnabled = true //включить перемещение по диаграмме (смещение графиков пальцем)
            setScaleEnabled(true) //включить масштабирование двумя пальцами
            //setPinchZoom(true) //включить одновременное изменение масштаба по X и Y
            setMaxVisibleValueCount(MAX_VISIBLE_VALUE)
        }
    }

    /** Задать настройки осей координат. */
    private fun setSettingAxes() {
        with(binding.lineChart) {
            axisLeft.isEnabled = true //включить подпись слева
            axisLeft.setDrawAxisLine(true) //показывать линию оси Y слева
            axisLeft.setDrawGridLines(true) //показывать горизонтальны линии по значениям оси Y слева
            axisLeft.textSize = AXIS_FONT_SIZE //размер текста
            axisLeft.isGranularityEnabled = true //отключить промежуточные значения по оси Y
            axisRight.isEnabled = false //отключить подпись справа
            axisRight.setDrawAxisLine(false) //отключить линию оси Y справа
            axisRight.setDrawGridLines(false) //отключить горизонтальны линии по значениям оси Y справа

            xAxis.setDrawAxisLine(true) //показывать линию оси X
            xAxis.setDrawGridLines(false) //отключить вертикальные линии по значениям оси X
            xAxis.position = XAxis.XAxisPosition.BOTTOM //подпись по оси X расположить снизу
            xAxis.textSize = AXIS_FONT_SIZE //размер текста
            xAxis.isGranularityEnabled = true //отключить промежуточные значения по оси X
        }
    }

    /** Задать настройки легенды графика */
    private fun setSettingLegend() {
        with(binding.lineChart.legend) {
            verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
            horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
            orientation = Legend.LegendOrientation.HORIZONTAL
            setDrawInside(false) //отключить расположение легенды внутри графика
            form = Legend.LegendForm.CIRCLE //задать форму цветовых меток
            formSize = LEGEND_INDICATOR_SIZE //размер цветовой метки
            textSize = LEGEND_FONT_SIZE //размер текста легенды
            isWordWrapEnabled = true //переносить метки на новую строку
            maxSizePercent = MAX_SIZE_PERCENT
        }
    }

    /** Задать настройки дополнительных линий на графике. */
    private fun setSettingLimitLines() {
        val zeroLimitLine = LimitLine(ZERO_VALUE)
        zeroLimitLine.lineColor = Color.BLUE
        zeroLimitLine.lineWidth = ZERO_LINE_WIDTH
        zeroLimitLine.enableDashedLine(ZERO_LINE_LENGTH, ZERO_LINE_LENGTH, ZERO_LINE_PHASE)
        binding.lineChart.axisLeft.addLimitLine(zeroLimitLine)
    }

    //обработка состояний
    private fun renderData(state: BaseState) {
        when (state) {
            //начало процесса загрузки
            is BaseState.Loading -> {
                binding.progressBar.visibleOrGone(true)
            }

            //полученные данные по графику
            is GraphicState.Success -> {
                showGraphic(state.chartGraphicDto)
                binding.progressBar.visibleOrGone(false)
            }

            //какая-то ошибка
            is BaseState.ErrorState -> Log.d(App.LOG_TAG, state.text)
        }
    }

    /** Отобразить график по полученным данным. */
    private fun showGraphic(chartGraphicDto: ChartGraphicDto) {
        setTitles(chartGraphicDto.chart)
        with(binding.lineChart) {
            resetTracking()
            xAxis.valueFormatter = chartGraphicDto.xValueFormatter
            data = chartGraphicDto.lineData

            zoomToCenter(chartGraphicDto.zoomScale, chartGraphicDto.zoomScale/2)
            binding.lineChart.moveViewTo(Float.MAX_VALUE, ZERO_VALUE, YAxis.AxisDependency.LEFT)
        }
    }

    /** Задать заголовки графика и осей координат. */
    private fun setTitles(chart: Chart) {
        binding.graphName.text = chart.name
        binding.yAxisName.text = chart.yName
        val description = Description()
        description.text = chart.xName
        binding.lineChart.description = description
    }
}