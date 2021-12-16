package ru.easygraphics.helpers.di

import androidx.room.Room
import org.koin.dsl.module
import ru.easygraphics.data.db.AppDB
import ru.easygraphics.helpers.consts.DB
import ru.easygraphics.mainWindow.ChartsListFragment

object Modules {
    //модуль, содержимое которого должно быть во всем приложении
    val application = module {
        single<AppDB> {
            Room.databaseBuilder(get(), AppDB::class.java, DB.NAME)
                .addCallback(AppDB.InsertDefaultData)
                .build()
        }
    }

    //модуль главного окна
    val mainWindow = module {
        scope<ChartsListFragment> {
        }
    }
}
