package ru.easygraphics.chartsettingsWindow

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.terrakok.cicerone.Router
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent
import org.koin.java.KoinJavaComponent.getKoin
import ru.easygraphics.databinding.FragmentChartDescriptionBinding
import ru.easygraphics.helpers.consts.Scopes

class ChartDescriptionFragment : Fragment() {
    private lateinit var binding: FragmentChartDescriptionBinding
    private val scope = getKoin().createScope<ChartDescriptionFragment>()
    private val router: Router = scope.get(qualifier = named(Scopes.ROUTER))
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

    }
}