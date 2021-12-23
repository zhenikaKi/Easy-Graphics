package ru.easygraphics.chartTableWindow

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.github.terrakok.cicerone.androidx.FragmentScreen

class TableScreen(val chart_id:Long): FragmentScreen {
    override fun createFragment(factory: FragmentFactory): Fragment = TableFragment.newInstance(chart_id)
}