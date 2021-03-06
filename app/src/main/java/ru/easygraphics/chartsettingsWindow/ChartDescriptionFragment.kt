package ru.easygraphics.chartsettingsWindow

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import com.pes.androidmaterialcolorpickerdialog.ColorPicker
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent.getKoin
import ru.easygraphics.R
import ru.easygraphics.baseobjects.BaseFragment
import ru.easygraphics.data.db.entities.Chart
import ru.easygraphics.data.db.entities.ChartLine
import ru.easygraphics.databinding.FragmentChartDescriptionBinding
import ru.easygraphics.extensions.toast
import ru.easygraphics.helpers.ColorConvert
import ru.easygraphics.helpers.consts.App
import ru.easygraphics.helpers.consts.DB
import ru.easygraphics.helpers.consts.Scopes
import ru.easygraphics.states.BaseState
import ru.easygraphics.states.DescriptionState
import ru.easygraphics.visibleOrGone
import kotlin.random.Random

class ChartDescriptionFragment :
    BaseFragment<FragmentChartDescriptionBinding>(FragmentChartDescriptionBinding::inflate) {

    private val scope = getKoin().createScope<ChartDescriptionFragment>()
    private val model: ChartDescriptionViewModel =
        scope.get(qualifier = named(Scopes.DESCRIPTION_VIEW_MODEL))

    private var chartId: Long? = null
    private var chart: Chart? = null
    private var lines: List<ChartLine>? = null
    private var linesDelete: List<Long>? = null

    companion object {
        fun newInstance(chartId: Long?): Fragment = ChartDescriptionFragment()
            .also {
                chartId?.let { id -> it.arguments = bundleOf(DB.CHART_ID to id) }
            }
    }

    override fun initAfterCreate() {
        chartId = arguments?.getLong(DB.CHART_ID)
    }

    override fun onResume() {
        super.onResume()
        //запускаем процесс получения данных по графику
        chartId?.let {
            model.loadGraphicData(it)
            disabledFieldsWhenNoEdit()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.linesBlock.removeAllViews()
        //добавляем одну линию по умолчанию для нового графика
        if (chartId == null) {
            addParamLine(line = null, hideIconDelete = true)
        }

        //связываем fragment с viewModel
        model.getLiveData().observe(viewLifecycleOwner, { renderData(it) })

        //задаем заголовок окна
        setTitle(if (chartId == null) R.string.title_new_graphic else R.string.title_edit_graphic)

        //формируем выпадающие списки
        setDropDownLists()

        //задаем обработчики кнопок
        setButtonListener()
    }

    /** Заблокировать поля, которые нельзя редактировать при редактировании графика */
    private fun disabledFieldsWhenNoEdit(xValueType: DB.ValueTypes? = null,
                                         xValueDateFormat: DB.DateTypes? = null) {
        //выбираем в списке нужный тип отображения подписи по X
        xValueType?.let { selectedXValueType(it) }
        //выбираем в списке нужный формат даты
        xValueType
            ?.takeIf { it == DB.ValueTypes.DATE }
            ?.let {
                xValueDateFormat?.let { dateType -> selectedDateFormat(dateType) }
            }

        //при редактировании нельзя менять тип подписи по X
        binding.inputXType.isEnabled = false
        binding.inputXDateFormat.isEnabled = false
    }

    /** Сохранение графика */
    override fun saveData() {
        //проверяем заполненность полей
        if (!validateEdits()) {
            return
        }

        //заполняем данные для сохранения
        setDataGraphicForSaved()
        setDataLinesForSaved()

        //сохраняем данные
        chart?.let { chartToSave ->
            lines?.let { linesToSave ->
                model.saveGraphicData(chartToSave, linesToSave, linesDelete)
            }
        }
        requireContext().toast(resources.getString(R.string.message_save))
    }

    /** Навешать обработчики на кнопки */
    private fun setButtonListener() {
        //кнопка добавления новой линии
        binding.buttonAddLine.setOnClickListener {
            addParamLine(line = null, hideIconDelete = false)
        }
    }

    /** Заполнить выпадающие списки нужными данными */
    private fun setDropDownLists() {
        //тип подписи по X
        val adapter: ArrayAdapter<String> = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            DB.ValueTypes.titles()
        )
        with(binding) {
            editXType.setAdapter(adapter)
            //навешаем обработчик на выбор типа подписи
            editXType.setOnItemClickListener { _, _, index, _ ->
                inputXDateFormat.isVisible = (DB.ValueTypes.values()[index] == DB.ValueTypes.DATE)
            }
        }

        //формат даты
        val adapterDate: ArrayAdapter<String> = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            DB.DateTypes.titles()
        )
        binding.editXDateFormat.setAdapter(adapterDate)
    }

    /**
     * Обработка состояний
     * @param state [BaseState] полученное состояние от [model]
     */
    private fun renderData(state: BaseState?) {
        when (state) {
            //загрузка основной информации
            BaseState.LoadingRoot -> {
                binding.progressBar.visibleOrGone(true)
            }

            BaseState.LoadingSaved -> {
                binding.progressBar.visibleOrGone(true)
            }

            BaseState.SavedWithTableOpening -> {
                binding.progressBarOnBottom.visibleOrGone(true)
            }

            //получены данные по редактируемому графику
            is DescriptionState.LoadData -> {
                showLoadedData(state.chart, state.lines)
                binding.progressBar.visibleOrGone(false)
            }

            //сохраненные данные
            is DescriptionState.Saved -> {
                showLoadedData(state.chart, state.lines)
                binding.progressBar.visibleOrGone(false)
            }

            //какая-то ошибка
            is BaseState.ErrorState -> Log.d(App.LOG_TAG, state.text)
        }

        //сделаем пустое уведомление, чтобы при возврате с предыдущего экрана
        //повторно не отрабатывало состояние выше
        model.clearState()
    }

    /** Выбор цвета */
    private fun setColorClickListener(view: View?) {
        val viewColor: ColorDrawable = view?.background as ColorDrawable
        val colorPicker = ColorPicker(
            requireActivity(),
            viewColor.color.red,
            viewColor.color.green,
            viewColor.color.blue
        )
        colorPicker.show()
        colorPicker.enableAutoClose()
        colorPicker.setCallback {
            view.setBackgroundColor(it)
        }
    }

    /**
     * Отобразить данные по графику для редактирования
     * @param chart [Chart] Данные по графику
     * @param lines [List]<[ChartLine]> Данные по линиям графика
     */
    private fun showLoadedData(chart: Chart, lines: List<ChartLine>) {
        this.chart = chart
        this.lines = lines
        this.chartId = chart.chartId
        disabledFieldsWhenNoEdit(chart.xValueType, chart.xValueDateFormat)

        with(binding) {
            editGraphicName.setText(chart.name)
            editXName.setText(chart.xName)
            editYName.setText(chart.yName)
            editCountNumberAfterDecimal.setText(chart.countDecimal.toString())
            linesBlock.removeAllViews()

            //добавляем линии
            for (ind in lines.indices) {
                addParamLine(line = lines[ind], hideIconDelete = ind == 0)
            }
        }
    }

    /**
     * Добавить параметры линии.
     * @param line [ChartLine]? данные по конкретной линии из базы, если линия есть
     * @param hideIconDelete [Boolean] true - скрыть иконку удаления настройки линии, false - не скрывать
     */
    private fun addParamLine(line: ChartLine?, hideIconDelete: Boolean) {
        val view = inflater?.inflate(R.layout.layout_columns, null)
        view?.let { lineView ->
            val iconDelete = lineView.findViewById<ImageView>(R.id.line_delete)
            val editName = lineView.findViewById<EditText>(R.id.edit_line_name)
            val colorLine = lineView.findViewById<View>(R.id.color_of_the_chart)
            //обрабатываем кнопку удаления строки
            if (hideIconDelete) {
                iconDelete.visibility = View.GONE
            }
            iconDelete.setOnClickListener { binding.linesBlock.removeView(lineView) }
            //задаем имя линии
            line?.let {
                editName.setText(it.name)
                it.lineId?.let { id -> editName.setTag(R.id.tag_line_id, id) }

                colorLine.setBackgroundColor(ColorConvert.hexToColor(it.color))
            } ?: colorLine.setBackgroundColor(setRandomColor())
            //обрабатываем цвет
            colorLine.setOnClickListener { setColorClickListener(it) }
            binding.linesBlock.addView(lineView)
        }
    }

    /**
     * Проверить заполненность полей.
     * @return true - все поля корректно заполнены, false - в каком-то поле есть ошибка
     */
    private fun validateEdits(): Boolean {
        var result = true
        val fieldNull = getString(R.string.field_null)
        with(binding) {
            //сформируем список полей для проверки
            val fields: MutableList<Pair<EditText, TextInputLayout>> = mutableListOf(
                Pair(editGraphicName, inputGraphicName),
                Pair(editXName, inputXName),
                Pair(editYName, inputYName),
                Pair(editCountNumberAfterDecimal, inputCountNumberAfterDecimal)
            )
            //добавляем тип подписи и формат даты
            if (chartId === null) {
                fields.add(Pair(editXType, inputXType))
                val xType = editXType.text.toString()
                if (xType.isNotEmpty()
                    && DB.ValueTypes.titleToValueTypes(xType) == DB.ValueTypes.DATE
                ) {
                    fields.add(Pair(editXDateFormat, inputXDateFormat))
                }
            }
            //добавляем название линий
            for (ind in 0 until linesBlock.childCount) {
                val lineView = linesBlock.getChildAt(ind)
                fields.add(
                    Pair(
                        lineView.findViewById(R.id.edit_line_name),
                        lineView.findViewById(R.id.input_line_name)
                    )
                )
            }

            //проверяем все поля
            fields.forEach { field ->
                if (field.first.text.toString().isEmpty()) {
                    result = false
                    field.second.error = fieldNull
                } else {
                    field.second.error = null
                }
            }

            //проверяем число в количестве знаков
            try {
                editCountNumberAfterDecimal.text.toString().toInt()
                inputCountNumberAfterDecimal.error = null
            } catch (e: Exception) {
                inputCountNumberAfterDecimal.error = getString(R.string.invalid_number)
            }
        }

        return result
    }

    /** Подготовить данные по графику к сохранению */
    private fun setDataGraphicForSaved() {
        with(binding) {
            chart = Chart(
                chartId = chartId,
                name = editGraphicName.text.toString(),
                countDecimal = editCountNumberAfterDecimal.text.toString().toInt(),
                xValueType = chart?.xValueType
                    ?: DB.ValueTypes.titleToValueTypes(editXType.text.toString())
                    ?: DB.ValueTypes.STRING,
                xValueDateFormat = chart?.xValueDateFormat ?: DB.DateTypes.titleToDateTypes(
                    editXType.text.toString()
                ),
                xName = editXName.text.toString(),
                yName = editYName.text.toString()
            )
        }
    }

    /** Подготовить данные по линиям к сохранению */
    private fun setDataLinesForSaved() {
        val linesTmp: MutableList<ChartLine> = mutableListOf()
        with(binding) {
            for (ind in 0 until linesBlock.childCount) {
                val lineView = linesBlock.getChildAt(ind)
                val editLineName = lineView.findViewById<EditText>(R.id.edit_line_name)
                val colorLineName = lineView.findViewById<View>(R.id.color_of_the_chart)
                val lineId = editLineName.getTag(R.id.tag_line_id)?.toString()?.toLong()
                linesTmp.add(
                    ChartLine(
                        lineId = lineId,
                        //после сохранения chart нужно обязательно обновлять этот id
                        chartId = chart?.chartId ?: -1,
                        name = editLineName.text.toString(),
                        color = ColorConvert.colorToHex((colorLineName.background as ColorDrawable).color)
                    )
                )
            }
        }
        //дополнительно формируем список линий, которые удалили
        linesDelete = null
        lines?.let { oldLines ->
            linesDelete = oldLines
                .filter { oldLine -> !linesTmp.any { newLine -> oldLine.lineId == newLine.lineId } }
                .mapNotNull { oldLine -> oldLine.lineId }
        }

        lines = linesTmp
    }

    /** Сформировать случайны цвет. */
    private fun setRandomColor(): Int {
        val random = Random.Default
        return Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256))
    }

    /**
     * Выбрать из списка нужный тип подписи по X.
     * @param xValueType тип подписи, который нужно выбрать.
     */
    private fun selectedXValueType(xValueType: DB.ValueTypes) {
        for (ind in DB.ValueTypes.values().indices) {
            if (xValueType == DB.ValueTypes.values()[ind]) {
                binding.editXType.setText(xValueType.title)
                break
            }
        }
    }

    /**
     * Выбрать из списка нужный формат даты для типа подписи [DB.ValueTypes.DATE].
     * @param dateType формат даты, который нужно выбрать.
     */
    private fun selectedDateFormat(dateType: DB.DateTypes) {
        for (ind in DB.DateTypes.values().indices) {
            if (dateType == DB.DateTypes.values()[ind]) {
                binding.inputXDateFormat.isVisible = true
                binding.editXDateFormat.setText(dateType.title)
                break
            }
        }
    }
}