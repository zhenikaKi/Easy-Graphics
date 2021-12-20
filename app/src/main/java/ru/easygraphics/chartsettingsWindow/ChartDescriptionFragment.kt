package ru.easygraphics.chartsettingsWindow

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.github.terrakok.cicerone.Router
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent
import ru.easygraphics.BaseFragment
import ru.easygraphics.databinding.FragmentChartDescriptionBinding
import ru.easygraphics.helpers.consts.Scopes
import ru.easygraphics.tableWindow.TableScreen

class ChartDescriptionFragment :
    BaseFragment<FragmentChartDescriptionBinding>(FragmentChartDescriptionBinding::inflate) {

    private val scope = KoinJavaComponent.getKoin().createScope<ChartDescriptionFragment>()
    private val router: Router = scope.get(qualifier = named(Scopes.ROUTER))

    companion object {
        fun newInstance(): Fragment = ChartDescriptionFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //закоментировал чтоб работало
        /*binding.buttonToTable.setOnClickListener {
            router.navigateTo(TableScreen())
        }*/
    }
}