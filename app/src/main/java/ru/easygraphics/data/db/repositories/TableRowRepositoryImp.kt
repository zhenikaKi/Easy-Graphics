package ru.easygraphics.data.db.repositories

import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent
import ru.easygraphics.data.db.AppDB
import ru.easygraphics.data.db.entities.ChartAllData
import ru.easygraphics.helpers.consts.Scopes
import ru.easygraphics.states.Success
import ru.easygraphics.states.Error
import ru.easygraphics.states.TableState
import ru.easygraphics.tableWindow.TableFragment

class TableRowRepositoryImp(
    private val db: AppDB
) : TableRowRepository {

    //private val scope = KoinJavaComponent.getKoin().createScope<TableFragment>()
    //private val db: AppDB = scope.get(qualifier = named(Scopes.DB))

    override suspend fun getAllDataOnChartId(chartId: Long): TableState<ChartAllData> {
        return try {
            val result = db.chartAllDataDao().getAllDataOnChartId(chartId = chartId)
            Success(result)
        } catch (ex: Exception) {
            Error(ex)
        }
    }
}