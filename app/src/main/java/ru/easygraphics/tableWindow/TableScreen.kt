package ru.easygraphics.tableWindow

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.github.terrakok.cicerone.androidx.FragmentScreen

class TableScreen(
    private val chartId: Long,
    private val chartName: String
) : FragmentScreen {

    override fun createFragment(factory: FragmentFactory): Fragment =
        TableFragment.newInstance(chartId, chartName)
}