package ru.easygraphics

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.terrakok.cicerone.NavigatorHolder
import com.github.terrakok.cicerone.Router
import com.github.terrakok.cicerone.androidx.AppNavigator
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent.getKoin
import ru.easygraphics.databinding.ActivityMainBinding
import ru.easygraphics.helpers.consts.Scopes
import ru.easygraphics.mainWindow.ChartListScreen

private lateinit var binding: ActivityMainBinding
class MainActivity : AppCompatActivity() {
    private val scope = getKoin().createScope<MainActivity>()

    private var navigatorHolder: NavigatorHolder = scope.get(qualifier = named(Scopes.NAVIGATOR))
    private val router: Router = scope.get(qualifier = named(Scopes.ROUTER))
    private val navigator = AppNavigator(this, R.id.container)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        router.newRootScreen(ChartListScreen())
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        super.onPause()
        navigatorHolder.removeNavigator()
    }
}