package ru.easygraphics.chartsettingsWindow

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import com.github.terrakok.cicerone.Router
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent.getKoin
import ru.easygraphics.databinding.FragmentChartDescriptionBinding
import ru.easygraphics.helpers.consts.Scopes

class ChartDescriptionFragment : Fragment() {
    private lateinit var binding: FragmentChartDescriptionBinding
    private val scope = getKoin().createScope<ChartDescriptionFragment>()
    private val router: Router = scope.get(qualifier = named(Scopes.ROUTER))
    private var list:ArrayList <Pair<EditText,View>> = arrayListOf()

    companion object {
        fun newInstance(chart_id:Int): Fragment{
            val cdfragment=ChartDescriptionFragment()
            cdfragment.arguments=Bundle().apply{putInt("chart_id",chart_id)}
            return cdfragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChartDescriptionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        for (i in 0..list.size-1){
            outState.putString("name_of_the_${i}_column",list[i].first.text.toString())
            outState.putInt("color_of_the_${i}_chart",(list[i].second.background as ColorDrawable).color)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.nameOfThe1Column.setText(savedInstanceState!!.getString("name_of_the_0_column"))
        binding.colorOfThe1Chart.setBackgroundColor(savedInstanceState!!.getInt("color_of_the_0_chart"))
        list.add(Pair(binding.nameOfThe1Column,binding.colorOfThe1Chart))
        var i=1
        if (savedInstanceState!=null) {
            while (savedInstanceState!!.getString("name_of_the_${i}_column") != null) {
                val llext = binding.namesOfYColumns
                val llint = LinearLayout(context)
                llint.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    30 * requireContext().resources.displayMetrics.density as Int
                )
                val et = EditText(context)
                val v = View(context)
                et.layoutParams = LinearLayout.LayoutParams(
                    200 * requireContext().resources.displayMetrics.density as Int,
                    30 * requireContext().resources.displayMetrics.density as Int
                )
                v.layoutParams = LinearLayout.LayoutParams(
                    30 * requireContext().resources.displayMetrics.density as Int,
                    30 * requireContext().resources.displayMetrics.density as Int
                )
                llint.addView(et)
                llint.addView(v)
                et.setText(savedInstanceState!!.getString("name_of_the_${i}_column"))
                v.setBackgroundColor(savedInstanceState!!.getInt("color_of_the_0_chart"))
                llext.addView(llint)
                list.add(Pair(et, v))
                i++
            }
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonCancelDescription.setOnClickListener { router.exit() }
        binding.buttonAddYColumn.setOnClickListener {
            val llext =binding.namesOfYColumns
            val llint = LinearLayout(context)
            llint.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,30*requireContext().resources.displayMetrics.density as Int)
            val et = EditText(context)
            val v = View(context)
            et.layoutParams = LinearLayout.LayoutParams(200*requireContext().resources.displayMetrics.density as Int,30*requireContext().resources.displayMetrics.density as Int)
            v.layoutParams = LinearLayout.LayoutParams(30*requireContext().resources.displayMetrics.density as Int,30*requireContext().resources.displayMetrics.density as Int)
            llint.addView(et)
            llint.addView(v)
            v.setBackgroundColor(0)
            llext.addView(llint)
            list.add(Pair(et,v))
        }
    }
}