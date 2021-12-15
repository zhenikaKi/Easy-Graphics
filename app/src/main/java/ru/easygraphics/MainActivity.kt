package ru.easygraphics

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.easygraphics.databinding.ActivityMainBinding
import ru.easygraphics.mainWindow.ChartsListFragment

private lateinit var binding: ActivityMainBinding
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportFragmentManager.beginTransaction().replace(R.id.container, ChartsListFragment()).commit()
    }
}