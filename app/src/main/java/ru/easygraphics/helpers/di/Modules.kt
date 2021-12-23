package ru.easygraphics.helpers.di

import androidx.room.Room
import com.github.terrakok.cicerone.Cicerone
import com.github.terrakok.cicerone.NavigatorHolder
import com.github.terrakok.cicerone.Router
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import ru.easygraphics.MainActivity
import ru.easygraphics.chartsettingsWindow.ChartDescriptionFragment
import ru.easygraphics.data.db.AppDB
import ru.easygraphics.data.db.repositories.DataRepository
import ru.easygraphics.data.db.repositories.LocalDbRepository
import ru.easygraphics.graphicwindow.GraphicFragment
import ru.easygraphics.graphicwindow.GraphicViewModel
import ru.easygraphics.helpers.consts.DB
import ru.easygraphics.helpers.consts.Scopes
import ru.easygraphics.mainWindow.ChartsListFragment

object Modules {
    //модуль, содержимое которого должно быть во всем приложении
    val application = module {
        //база данных
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

        //работа с данными
        single<DataRepository>(qualifier = named(Scopes.DATA_REPOSITORY)) {
            LocalDbRepository(get(qualifier = named(Scopes.DB)))
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

    //модуль окна описания графика
    val descriptionWindow = module {
        scope<ChartDescriptionFragment>{
        }
    }

    //модуль окна с графиком
    val graphicWindow = module {
        scope<GraphicFragment> {
            viewModel(qualifier = named(Scopes.GRAPHIC_VIEW_MODEL)) {
                GraphicViewModel(get(qualifier = named(Scopes.DATA_REPOSITORY)))
            }
        }
    }
}
