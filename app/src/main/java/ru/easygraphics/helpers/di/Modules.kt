package ru.easygraphics.helpers.di

import androidx.room.Room
import com.github.terrakok.cicerone.Cicerone
import com.github.terrakok.cicerone.NavigatorHolder
import com.github.terrakok.cicerone.Router
import org.koin.core.qualifier.named
import org.koin.dsl.module
import ru.easygraphics.MainActivity
import ru.easygraphics.chartsettingsWindow.ChartDescriptionFragment
import ru.easygraphics.data.db.AppDB
import ru.easygraphics.helpers.consts.DB
import ru.easygraphics.helpers.consts.Scopes
import ru.easygraphics.mainWindow.ChartsListFragment
import ru.easygraphics.tableWindow.TableFragment

object Modules {
    //модуль, содержимое которого должно быть во всем приложении
    val application = module {
        single<AppDB>(qualifier = named(Scopes.DB)) {
            Room.databaseBuilder(get(), AppDB::class.java, DB.NAME)
                .addCallback(AppDB.InsertDefaultData)
                .build()
        }

        //навигация
        single<Cicerone<Router>>(qualifier = named(Scopes.CICERONE)) {
            Cicerone.create(Router())
        }
        single<NavigatorHolder>(qualifier = named(Scopes.NAVIGATOR)) {
            get<Cicerone<Router>>(qualifier = named(Scopes.CICERONE)).getNavigatorHolder()
        }
        single<Router>(qualifier = named(Scopes.ROUTER)) {
            get<Cicerone<Router>>(qualifier = named(Scopes.CICERONE)).router
        }
    }

    //модуль основной активити
    val appActivity = module {
        scope<MainActivity> {
        }
    }

    //модуль главного окна
    val mainWindow = module {
        scope<ChartsListFragment> {
        }
    }

    val chartDescriptionWindow = module {
        scope<ChartDescriptionFragment> {
        }
    }

    /*val tableWindow = module {
        scope<TableFragment> {
        }
    }*/
}
