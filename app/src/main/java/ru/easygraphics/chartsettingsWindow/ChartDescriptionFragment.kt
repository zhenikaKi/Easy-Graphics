package ru.easygraphics.chartsettingsWindow

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.github.terrakok.cicerone.Router
import com.google.android.material.textfield.TextInputLayout
import com.pes.androidmaterialcolorpickerdialog.ColorPicker
import kotlinx.android.synthetic.main.layout_columns.view.*
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent.getKoin
import ru.easygraphics.R
import ru.easygraphics.baseobjects.BaseFragment
import ru.easygraphics.data.db.AppDB
import ru.easygraphics.data.db.converts.DateTypesConvert
import ru.easygraphics.data.db.converts.ValueTypesConvert
import ru.easygraphics.data.db.entities.Chart
import ru.easygraphics.data.db.entities.ChartLine
import ru.easygraphics.databinding.FragmentChartDescriptionBinding
import ru.easygraphics.helpers.ColorConvert
import ru.easygraphics.helpers.consts.App
import ru.easygraphics.helpers.consts.DB
import ru.easygraphics.helpers.consts.Scopes
import ru.easygraphics.states.BaseState
import ru.easygraphics.states.DescriptionState
import ru.easygraphics.states.LoadingTypes
import ru.easygraphics.tableWindow.TableScreen
import ru.easygraphics.tabletest.TableTestScreen

class ChartDescriptionFragment :
    BaseFragment<FragmentChartDescriptionBinding>(FragmentChartDescriptionBinding::inflate) {

    private val scope = getKoin().createScope<ChartDescriptionFragment>()
    private val model: ChartDescriptionViewModel = scope.get(qualifier = named(Scopes.DESCRIPTION_VIEW_MODEL))
    private val router: Router = scope.get(qualifier = named(Scopes.ROUTER))

    private val db: AppDB = scope.get(qualifier = named(Scopes.DB))
    private var list: ArrayList<Pair<EditText, View>> = arrayListOf()

    private val chartId by lazy { arguments?.getLong(DB.CHART_ID) }
    private var chart: Chart? = null
    private var lines: List<ChartLine>? = null

    companion object {
        fun newInstance(chartId: Long?): Fragment = ChartDescriptionFragment()
            .also {
                chartId?.let { id -> it.arguments = bundleOf(DB.CHART_ID to id) }
            }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
//        for (i in 0 until list.size) {
//            outState.putString("name_of_the_${i}_column", list[i].first.text.toString())
//            outState.putInt(
//                "color_of_the_${i}_chart",
//                (list[i].second.background as ColorDrawable).color
//            )
//        }
//        outState.putString("chartName", binding.chartName.text.toString())
//        outState.putString("x_axis_signature", binding.xAxisSignature.text.toString())
//        outState.putString("y_axis_signature", binding.yAxisSignature.text.toString())
//        outState.putString(
//            "number_of_digits_after_decimal_point",
//            binding.numberOfDigitsAfterDecimalPoint.text.toString()
//        )
//        outState.putInt("values_type_Y", binding.valuesTypeY.selectedItemPosition)
//        outState.putInt("date_format", binding.dateFormat.selectedItemPosition)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        list.add(Pair(binding.layoutColumns.nameOfTheColumn, binding.layoutColumns.colorOfTheChart))
//        binding.layoutColumns.colorOfTheChart.setOnClickListener {
//            setColorClickListener(it)
//        }

        binding.linesBlock.removeAllViews()

        //связываем fragment с viewModel
        model.getLiveData().observe(viewLifecycleOwner, { renderData(it) })

        //запускаем процесс получения данных по графику
        chartId?.let {
            model.loadGraphicData(it)
            //при редактировании нельзя менять тип подписи
            binding.inputXType.visibility = View.GONE
            binding.inputXDateFormat.visibility = View.GONE
        } ?:let {
            //сразу по умолчанию добавляем линию
            addParamLine(line = null, hideIconDelete = true)
        }

        //задаем заголовок окна
        setTitle(if (chartId == null) R.string.title_new_graphic else R.string.title_edit_graphic)

        //формируем выпадающие списки
        setDropDownLists()

        //задаем обработчики кнопок
        setButtonListener()

//        savedInstanceState?.let {
//            with(binding) {
//                chartName.setText(it.getString("chartName"))
//                xAxisSignature.setText(it.getString("x_axis_signature"))
//                yAxisSignature.setText(it.getString("y_axis_signature"))
//                numberOfDigitsAfterDecimalPoint.setText(it.getString("number_of_digits_after_decimal_point"))
//                valuesTypeY.setSelection(it.getInt("values_type_Y"))
//                dateFormat.setSelection(it.getInt("date_format"))
//                layoutColumns.nameOfTheColumn.setText(it.getString("name_of_the_0_column"))
//                layoutColumns.colorOfTheChart.setBackgroundColor(it.getInt("color_of_the_0_chart"))
//            }
//            var i = 1
//            while (it.getString("name_of_the_${i}_column") != null) {
//                val llext = binding.namesOfYColumns
//                val llint: LinearLayout =
//                    LinearLayout.inflate(context, R.layout.layout_columns, null) as LinearLayout
//                val et = llint.name_of_the_column
//                val v = llint.color_of_the_chart
//                et.setText(it.getString("name_of_the_${i}_column"))
//                v.setBackgroundColor(it.getInt("color_of_the_${i}_chart"))
//                v.setOnClickListener { view ->
//                    setColorClickListener(view)
//                }
//                llext.addView(llint)
//                list.add(Pair(et, v))
//                i++
//            }
//        }
//
//        binding.buttonCancelDescription.setOnClickListener { router.exit() }
//        binding.buttonAddYColumn.setOnClickListener {
//            val llext = binding.namesOfYColumns
//            val llint: LinearLayout =
//                LinearLayout.inflate(context, R.layout.layout_columns, null) as LinearLayout
//            val et = llint.name_of_the_column
//            val v = llint.color_of_the_chart
//            v.setBackgroundColor(Color.BLACK)
//            v.setOnClickListener {
//                setColorClickListener(it)
//            }
//            llext.addView(llint)
//            list.add(Pair(et, v))
//        }
//        binding.buttonToTable.setOnClickListener {
//            if (binding.chartName.text.toString() == "") return@setOnClickListener
//            model.saveDataToDB(Chart(
//                chartId,
//                binding.chartName.text.toString(),
//                binding.numberOfDigitsAfterDecimalPoint.text.toString().toInt(),
//                ValueTypesConvert().valueToEnum(binding.valuesTypeY.selectedItemPosition),
//                DateTypesConvert().valueToEnum(binding.dateFormat.selectedItemPosition)
//            ),list.map{pair->Pair(pair.first.text.toString(),(pair.second.background as ColorDrawable).color)})
//        }
    }



    /** Навешать обработчики на кнопки */
    private fun setButtonListener() {
        //кнопка добавления новой линии
        binding.buttonAddLine.setOnClickListener {
            addParamLine(line = null, hideIconDelete = false)
        }

        //кнопка перехода кданным графика
        binding.buttonTable.setOnClickListener {
            //todo сохранить линии и передать id графика
            router.navigateTo(TableTestScreen())
        }
    }

    /** Заполнить выпадающие списки нужными данными */
    private fun setDropDownLists() {
        //тип подписи по X
        val adapter: ArrayAdapter<String> = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            DB.ValueTypes.values().map { valueTypes -> valueTypes.title }
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
            DB.DateTypes.values().map { valueTypes -> valueTypes.title }
        )
        binding.editXDateFormat.setAdapter(adapterDate)
    }

    /**
     * Обработка состояний
     * @param state [BaseState] полученное состояние от [model]
     */
    private fun renderData(state: BaseState) {
        when (state) {
            //начало процесса загрузки
            is BaseState.Loading -> {
                when  (state.status) {
                    //загрузка основной информации
                    LoadingTypes.ROOT_DATA -> {

                    }
                }
            }

            //получены данные по редактируемому графику
            is DescriptionState.LoadData -> showLoadedData(state.chart, state.lines)

            //получен id
            //is DescriptionState.Success -> router.navigateTo(TableScreen(1, binding.chartName.text.toString()))

            //какая-то ошибка
            is BaseState.ErrorState -> Log.d(App.LOG_TAG, state.text)
        }
    }

    private fun setColorClickListener(v: View?) {
        val cp = ColorPicker(requireActivity(), 255, 0, 0, 0)
        cp.show()
        cp.enableAutoClose()
        cp.setCallback {
            v?.setBackgroundColor(it)
        }
    }

    /**
     * Отображение данных по графику для редактирования
     * @param chart [Chart] Данные по графику
     * @param lines [List]<[ChartLine]> Данные по линиям графика
     */
    private fun showLoadedData(chart: Chart, lines: List<ChartLine>) {
        this.chart = chart
        this.lines = lines

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

    /** Добавить параметры линии */
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
            iconDelete.setOnClickListener {  binding.linesBlock.removeView(lineView) }
            //задаем имя линии
            line?.let {
                editName.setText(it.name)
                it.lineId?.let { id -> editName.setTag(R.id.tag_line_id, id) }

                colorLine.setBackgroundColor(ColorConvert.hexToColor(it.color))
            } ?: colorLine.setBackgroundColor(Color.BLACK)
            //обрабатываем цвет
            colorLine.setOnClickListener { setColorClickListener(it) }

            binding.linesBlock.addView(lineView)
        }
    }

}