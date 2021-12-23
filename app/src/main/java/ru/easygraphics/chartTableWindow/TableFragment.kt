package ru.easygraphics.chartTableWindow

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import ru.easygraphics.R
import ru.easygraphics.baseobjects.BaseFragment
import ru.easygraphics.databinding.FragmentTableBinding
import ru.easygraphics.helpers.consts.DB


class TableFragment : BaseFragment<FragmentTableBinding>(FragmentTableBinding::inflate) {




    companion object {
        fun newInstance(chart_id:Long) =
            TableFragment().apply {
                arguments = bundleOf(DB.CHART_ID to chart_id)
            }
    }
}