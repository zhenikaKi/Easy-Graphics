package ru.easygraphics.settingwindow

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.github.terrakok.cicerone.androidx.FragmentScreen

class SettingScreen(): FragmentScreen {
    override fun createFragment(factory: FragmentFactory): Fragment = SettingFragment()
}