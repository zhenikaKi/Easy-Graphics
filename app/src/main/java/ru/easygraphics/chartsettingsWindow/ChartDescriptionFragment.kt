package ru.easygraphics.chartsettingsWindow

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonCancelDescription.setOnClickListener { router.exit() }
        list.add(Pair(binding.nameOf1Column,binding.colorOfThe1Chart))
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
            llext.addView(llint)
            list.add(Pair(et,v))
        }
    }
}