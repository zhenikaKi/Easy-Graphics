package ru.easygraphics.graphicwindow

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.os.bundleOf
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent.getKoin
import ru.easygraphics.R
import ru.easygraphics.baseobjects.BaseFragment
import ru.easygraphics.data.db.entities.Chart
import ru.easygraphics.data.db.entities.ChartAllDataViewed
import ru.easygraphics.databinding.FragmentGraphicBinding
import ru.easygraphics.helpers.ColorConvert
import ru.easygraphics.helpers.XValueFormatter
import ru.easygraphics.helpers.consts.App
import ru.easygraphics.helpers.consts.DB
import ru.easygraphics.helpers.consts.Scopes
import ru.easygraphics.states.BaseState
import ru.easygraphics.states.GraphicState
import ru.easygraphics.visibleOrGone
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.max
import kotlin.math.min

class GraphicFragment :
    BaseFragment<FragmentGraphicBinding>(FragmentGraphicBinding::inflate)
{
    private val scope = getKoin().createScope<GraphicFragment>()

    private val model: GraphicViewModel = scope.get(qualifier = named(Scopes.GRAPHIC_VIEW_MODEL))

    //id графика для отображения
    private val chartId by lazy { arguments?.getLong(DB.CHART_ID) }

    companion object {
        //примерное количество точек на графике по оси X
        private const val APPROXIMATE_QUANTITY_POINT = 25
        //размер масштаба графика по умолчанию
        private const val DEFAULT_ZOOM_SCALE = 1f
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
        //толщина линии на графике
        private const val LINE_WIDTH = 2.5f
        //размер точки линии на графике
        private const val LINE_POINT_SIZE = 4f
        //размер текста значения около точки линии на графике
        private const val LINE_POINT_VALUE_SIZE = 10f

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

            setTouchEnabled(true) //включить обработку нажатий пальцами (масштабирование, перемещение)
            isDragEnabled = true //включить перемещение по диаграмме (смещение графиков пальцем)
            setScaleEnabled(true) //включить масштабирование двумя пальцами
            //setPinchZoom(true) //включить одновременное изменение масштаба по X и Y
            setMaxVisibleValueCount(MAX_VISIBLE_VALUE)

            //настройка легенды
            with(legend) {
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

            val zeroLimitLine = LimitLine(ZERO_VALUE)
            zeroLimitLine.lineColor = Color.BLUE
            zeroLimitLine.lineWidth = ZERO_LINE_WIDTH
            zeroLimitLine.enableDashedLine(ZERO_LINE_LENGTH, ZERO_LINE_LENGTH, ZERO_LINE_PHASE)
            axisLeft.addLimitLine(zeroLimitLine)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle(R.string.title_graphic)

        //связываем fragment с viewModel
        model.getLiveData().observe(viewLifecycleOwner, { renderData(it) })

        //запускаем процесс получения данных по графику
        chartId?.let { model.loadGraphicData(it) }
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
                showGraphic(state.data)
                binding.progressBar.visibleOrGone(false)
            }

            //какая-то ошибка
            is BaseState.ErrorState -> Log.d(App.LOG_TAG, state.text)
        }
    }

    //отобразить график по полученным данным
    private fun showGraphic(data: ChartAllDataViewed) {
        binding.lineChart.resetTracking()

        //сформируем список подписей для оси X
        var dateFormat: String? = null
        if (data.chart.xValueType == DB.ValueTypes.DATE) {
            data.chart.xValueDateFormat?.let { dateFormat = it.dateFormat }
        }
        //todo по хорошему бы весь код ниже вынести в GraphicViewModel
        binding.lineChart.xAxis.valueFormatter =
            XValueFormatter(data.values.map { hV ->
                convertXAxisValue(dateFormat, hV.horizontalValue.value)
            })

        //начинаем формировать данные для графика
        val dataSets: ArrayList<ILineDataSet> = ArrayList()
        val lineValues: MutableMap<Int, ArrayList<Entry>> = HashMap() //список значений для каждой линии
        val maxXValues = data.values.size
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

                val yValue: Float? = hV.verticalValues[lineInd]?.value?.toFloat()
                yValue?.let { arrayList.add(Entry(ind.toFloat(), it)) }

                lineValues[lineInd] = arrayList
            }
        }

        //настраиваем линии
        for (lineInd in data.lines.indices) {
            val lineDataSet = LineDataSet(lineValues[lineInd], data.lines[lineInd].name)
            lineDataSet.lineWidth = LINE_WIDTH //толщина линии
            lineDataSet.circleRadius = LINE_POINT_SIZE //размер точки значения на линии
            lineDataSet.valueTextSize = LINE_POINT_VALUE_SIZE //рамер значения на самой линии
            lineDataSet.color = ColorConvert.hexToColor(data.lines[lineInd].color)
            lineDataSet.setCircleColor(ColorConvert.hexToColor(data.lines[lineInd].color))
            dataSets.add(lineDataSet)
        }

        val lineData = LineData(dataSets)
        binding.lineChart.data = lineData
        //вычисляем масштаб такой, чтобы примерно по оси X было 25 точек
        val zoomScale = max(DEFAULT_ZOOM_SCALE, (maxXValues / APPROXIMATE_QUANTITY_POINT).toFloat())
        binding.lineChart.zoomToCenter(zoomScale, zoomScale/2)
        binding.lineChart.moveViewTo(Float.MAX_VALUE, ZERO_VALUE, YAxis.AxisDependency.LEFT)
    }

    /**
     * Преобразовать подпись по оси X к нужному виду с учетом формата отображения даты.
     * @param dateFormat формат отображения даты при [Chart.xValueType] = [DB.ValueTypes.DATE].
     * @param value исходнаяподпись значения.
     * @return значение в виде преобразованной даты, либо исходное значение.
     */
    private fun convertXAxisValue(dateFormat: String?, value: String): String {
        dateFormat?.let { pattern ->
            try {
                val mainDateFormat = SimpleDateFormat(App.DATE_FORMAT, Locale.getDefault())
                val newDateFormat = SimpleDateFormat(pattern, Locale.getDefault())
                val date = mainDateFormat.parse(value)
                date?.let { return newDateFormat.format(it) }
            } catch (e: Exception) {
                return value
            }
        }

        return value
    }
}