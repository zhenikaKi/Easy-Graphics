package ru.easygraphics.chartsettingsWindow

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.github.terrakok.cicerone.Router
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
import ru.easygraphics.databinding.FragmentChartDescriptionBinding
import ru.easygraphics.helpers.consts.App
import ru.easygraphics.helpers.consts.DB
import ru.easygraphics.helpers.consts.Scopes
import ru.easygraphics.states.BaseState
import ru.easygraphics.states.DescriptionState
import ru.easygraphics.tableWindow.TableScreen

class ChartDescriptionFragment :
    BaseFragment<FragmentChartDescriptionBinding>(FragmentChartDescriptionBinding::inflate) {
    private val scope = getKoin().createScope<ChartDescriptionFragment>()
    private val model: ChartDescriptionViewModel = scope.get(qualifier = named(Scopes.DESCRIPTION_VIEW_MODEL))
    private val router: Router = scope.get(qualifier = named(Scopes.ROUTER))
    private val db: AppDB = scope.get(qualifier = named(Scopes.DB))
    private var list: ArrayList<Pair<EditText, View>> = arrayListOf()
    private val chartId by lazy { arguments?.getLong(DB.CHART_ID) }
    companion object {
        fun newInstance(chartId: Long?): Fragment = ChartDescriptionFragment()
            .also {
                chartId?.let { id -> it.arguments = bundleOf(DB.CHART_ID to id) }
            }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        for (i in 0 until list.size) {
            outState.putString("name_of_the_${i}_column", list[i].first.text.toString())
            outState.putInt(
                "color_of_the_${i}_chart",
                (list[i].second.background as ColorDrawable).color
            )
        }
        outState.putString("chartName", binding.chartName.text.toString())
        outState.putString("x_axis_signature", binding.xAxisSignature.text.toString())
        outState.putString("y_axis_signature", binding.yAxisSignature.text.toString())
        outState.putString(
            "number_of_digits_after_decimal_point",
            binding.numberOfDigitsAfterDecimalPoint.text.toString()
        )
        outState.putInt("values_type_Y", binding.valuesTypeY.selectedItemPosition)
        outState.putInt("date_format", binding.dateFormat.selectedItemPosition)
    }

    private fun renderData(state: BaseState) {
        when (state) {
            //начало процесса загрузки
            is BaseState.Loading -> { }

            //получен id
            is DescriptionState.Success -> router.navigateTo(TableScreen(1, binding.chartName.text.toString())) 

            //какая-то ошибка
            is BaseState.ErrorState -> Log.d(App.LOG_TAG, state.text)
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model.getLiveData().observe(viewLifecycleOwner,{renderData(it)})
        list.add(Pair(binding.layoutColumns.nameOfTheColumn, binding.layoutColumns.colorOfTheChart))
        binding.layoutColumns.colorOfTheChart.setOnClickListener {
            setColorClickListener(it)
        }
        savedInstanceState?.let {
            with(binding) {
                chartName.setText(it.getString("chartName"))
                xAxisSignature.setText(it.getString("x_axis_signature"))
                yAxisSignature.setText(it.getString("y_axis_signature"))
                numberOfDigitsAfterDecimalPoint.setText(it.getString("number_of_digits_after_decimal_point"))
                valuesTypeY.setSelection(it.getInt("values_type_Y"))
                dateFormat.setSelection(it.getInt("date_format"))
                layoutColumns.nameOfTheColumn.setText(it.getString("name_of_the_0_column"))
                layoutColumns.colorOfTheChart.setBackgroundColor(it.getInt("color_of_the_0_chart"))
            }
            var i = 1
            while (it.getString("name_of_the_${i}_column") != null) {
                val llext = binding.namesOfYColumns
                val llint: LinearLayout =
                    LinearLayout.inflate(context, R.layout.layout_columns, null) as LinearLayout
                val et = llint.name_of_the_column
                val v = llint.color_of_the_chart
                et.setText(it.getString("name_of_the_${i}_column"))
                v.setBackgroundColor(it.getInt("color_of_the_${i}_chart"))
                v.setOnClickListener { view ->
                    setColorClickListener(view)
                }
                llext.addView(llint)
                list.add(Pair(et, v))
                i++
            }
        }

        binding.buttonCancelDescription.setOnClickListener { router.exit() }
        binding.buttonAddYColumn.setOnClickListener {
            val llext = binding.namesOfYColumns
            val llint: LinearLayout =
                LinearLayout.inflate(context, R.layout.layout_columns, null) as LinearLayout
            val et = llint.name_of_the_column
            val v = llint.color_of_the_chart
            v.setBackgroundColor(Color.BLACK)
            v.setOnClickListener {
                setColorClickListener(it)
            }
            llext.addView(llint)
            list.add(Pair(et, v))
        }
        binding.buttonToTable.setOnClickListener {
            if (binding.chartName.text.toString() == "") return@setOnClickListener
            model.saveDataToDB(Chart(
                chartId,
                binding.chartName.text.toString(),
                binding.numberOfDigitsAfterDecimalPoint.text.toString().toInt(),
                ValueTypesConvert().valueToEnum(binding.valuesTypeY.selectedItemPosition),
                DateTypesConvert().valueToEnum(binding.dateFormat.selectedItemPosition)
            ),list.map{pair->Pair(pair.first.text.toString(),(pair.second.background as ColorDrawable).color)})
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

}