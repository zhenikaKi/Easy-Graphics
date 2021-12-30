package ru.easygraphics.tabletest

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.github.terrakok.cicerone.androidx.FragmentScreen

class TableTestScreen(val chartId: Long): FragmentScreen {
    override fun createFragment(factory: FragmentFactory): Fragment =
        TableTestFragment.newInstance(chartId)
}