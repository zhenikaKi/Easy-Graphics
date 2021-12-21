package ru.easygraphics
import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import ru.easygraphics.helpers.di.Modules

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(
                Modules.application,
                Modules.appActivity,
                Modules.mainWindow,
                Modules.chartDescriptionWindow
                //Modules.tableWindow
            )
        }
    }
}
