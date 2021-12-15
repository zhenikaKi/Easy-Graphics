package ru.easygraphics

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.easygraphics.databinding.FragmentChartsListBinding

class ChartsListFragment : Fragment() {
    private lateinit var binding: FragmentChartsListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChartsListBinding.inflate(inflater, container, false)
        return binding.root
    }

}