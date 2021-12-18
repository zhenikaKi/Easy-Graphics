package ru.easygraphics.mainWindow

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.github.terrakok.cicerone.androidx.FragmentScreen

class ChartListScreen: FragmentScreen {
    override fun createFragment(factory: FragmentFactory): Fragment = ChartsListFragment.newInstance()
}