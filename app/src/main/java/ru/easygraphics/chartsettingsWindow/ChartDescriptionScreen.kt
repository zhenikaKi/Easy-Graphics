package ru.easygraphics.chartsettingsWindow

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.github.terrakok.cicerone.androidx.FragmentScreen

class ChartDescriptionScreen(val chart_id:Int): FragmentScreen {
    override fun createFragment(factory: FragmentFactory): Fragment = ChartDescriptionFragment.newInstance(chart_id)
}