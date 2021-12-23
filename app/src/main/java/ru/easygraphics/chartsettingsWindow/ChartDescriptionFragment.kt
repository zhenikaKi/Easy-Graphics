package ru.easygraphics.chartsettingsWindow

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import androidx.annotation.InspectableProperty
import androidx.fragment.app.Fragment
import com.github.terrakok.cicerone.Router
import com.pes.androidmaterialcolorpickerdialog.ColorPicker
import kotlinx.android.synthetic.main.layout_columns.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent.getKoin
import ru.easygraphics.baseobjects.BaseFragment
import ru.easygraphics.R
import ru.easygraphics.chartTableWindow.TableScreen
import ru.easygraphics.data.db.AppDB
import ru.easygraphics.data.db.converts.DateTypesConvert
import ru.easygraphics.data.db.converts.ValueTypesConvert
import ru.easygraphics.data.db.entities.Chart
import ru.easygraphics.data.db.entities.ChartLine
import ru.easygraphics.databinding.FragmentChartDescriptionBinding
import ru.easygraphics.helpers.ColorConvert
import ru.easygraphics.helpers.consts.DB
import ru.easygraphics.helpers.consts.Scopes

class ChartDescriptionFragment :
    BaseFragment<FragmentChartDescriptionBinding>(FragmentChartDescriptionBinding::inflate) {

    private val scope = getKoin().createScope<ChartDescriptionFragment>()
    private val router: Router = scope.get(qualifier = named(Scopes.ROUTER))
    private val db: AppDB = scope.get(qualifier = named(Scopes.DB))
    private var list: ArrayList<Pair<EditText, View>> = arrayListOf()

    companion object {
        fun newInstance(chart_id: Long): Fragment {
            val cdfragment = ChartDescriptionFragment()
            cdfragment.arguments = Bundle().apply {
                putLong(DB.CHART_ID, chart_id)
            }
            return cdfragment
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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
            var chart_id = requireArguments().getLong(DB.CHART_ID)
            val coroutineScope = CoroutineScope(Dispatchers.IO+ SupervisorJob())
            coroutineScope.launch {
                if (chart_id == -1L) {
                    db.chartDao().save(
                        Chart(
                            null,
                            binding.chartName.toString(),
                            binding.numberOfDigitsAfterDecimalPoint.toString().toInt(),
                            ValueTypesConvert().valueToEnum(binding.valuesTypeY.selectedItemPosition),
                            DateTypesConvert().valueToEnum(binding.dateFormat.selectedItemPosition)
                        )
                    )
                    chart_id=db.chartDao().getCharts()[db.chartDao().getCharts().size-1].chartId!!
                    for (i in list.indices) {
                        db.chartLineDao().save(ChartLine(null, chart_id,list[i].first.text.toString(),
                            ColorConvert.colorToHex((list[i].second.background as ColorDrawable).color)))
                    }
                    //сохраняем новую запись в таблицы Chart и ChartLine
                    //chart_id = новое значение chart_id в таблице
                } else {
                    db.chartDao().save(
                        Chart(
                            chart_id,
                            binding.chartName.toString(),
                            binding.numberOfDigitsAfterDecimalPoint.toString().toInt(),
                            ValueTypesConvert().valueToEnum(binding.valuesTypeY.selectedItemPosition),
                            DateTypesConvert().valueToEnum(binding.dateFormat.selectedItemPosition)
                        )
                    )
                    val cl=db.chartLineDao().getLines(chart_id)
                    for (i in cl.indices){
                        db.chartLineDao().delete(cl[i])
                    }
                    for (i in list.indices) {
                        db.chartLineDao().save(ChartLine(null, chart_id,list[i].first.text.toString(),
                            ColorConvert.colorToHex((list[i].second.background as ColorDrawable).color)))
                    }
                    //изменяем запись в таблицах Chart и ChartLine
                }
            }
            router.navigateTo(TableScreen(chart_id))
            //router.navigateTo(TableScreen(chart_id)),
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