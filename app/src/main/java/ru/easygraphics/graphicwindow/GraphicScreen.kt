package ru.easygraphics.graphicwindow

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.github.terrakok.cicerone.androidx.FragmentScreen

class GraphicScreen(val chartId: Long): FragmentScreen {
    override fun createFragment(factory: FragmentFactory): Fragment =
        GraphicFragment.newInstance(chartId)
}