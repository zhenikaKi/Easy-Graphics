package ru.easygraphics.tableWindow

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.flow.collect
import org.koin.java.KoinJavaComponent.getKoin
import org.koin.core.qualifier.named
import ru.easygraphics.arguments
import ru.easygraphics.baseobjects.BaseFragment
import ru.easygraphics.databinding.FragmentTableBinding
import ru.easygraphics.helpers.consts.Scopes
import ru.easygraphics.tableWindow.adapter.TableAdapter
import ru.easygraphics.toast

class TableFragment : BaseFragment<FragmentTableBinding>(FragmentTableBinding::inflate) {

    companion object {
        private const val ARG_CHART_ID = "argument_chart_id"
        private const val ARG_CHART_NAME = "argument_chart_name"

        fun newInstance(chartId: Long, chartName: String): Fragment =
            TableFragment()
                .arguments(
                    ARG_CHART_ID to chartId,
                    ARG_CHART_NAME to chartName
                )
    }

    private val chartId: Long? by lazy {
        arguments?.getLong(ARG_CHART_ID, 1)
    }

    private val chartName: String by lazy {
        arguments?.getString(ARG_CHART_NAME).orEmpty()
    }

    private val scope = getKoin().createScope<TableFragment>()
    private val tableViewModel: TableViewModel =
        scope.get(qualifier = named(Scopes.TABLE_VIEW_MODEL))

    //private val router: Router = scope.get(qualifier = named(Scopes.ROUTER))

    private val adapter: TableAdapter = TableAdapter()

    /*private val tableViewModel: TableViewModel =
        TableViewModel(repository = TableRowRepositoryImp())*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            chartId?.let { tableViewModel.fetchTableRows(chartId = it) }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.graphName.text = chartName
        binding.table.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            tableViewModel.error.collect {
                this@TableFragment.toast(it)
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            tableViewModel.rowData.collect { rowData ->
                adapter.submitList(rowData)
            }
        }
    }
}