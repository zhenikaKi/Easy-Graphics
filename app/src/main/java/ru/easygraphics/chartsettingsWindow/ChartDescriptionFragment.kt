package ru.easygraphics.chartsettingsWindow

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.easygraphics.R
import ru.easygraphics.databinding.FragmentChartDescriptionBinding
import ru.easygraphics.databinding.FragmentChartsListBinding

class ChartDescriptionFragment : Fragment() {
    private lateinit var binding: FragmentChartDescriptionBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChartDescriptionBinding.inflate(inflater, container, false)
        return binding.root
    }
}