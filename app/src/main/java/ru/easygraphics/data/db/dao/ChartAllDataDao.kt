package ru.easygraphics.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import ru.easygraphics.data.db.entities.ChartAllData
import ru.easygraphics.data.db.entities.ChartAndLines
import ru.easygraphics.helpers.consts.DB

@Dao
interface ChartAllDataDao {
    @Transaction
    @Query("select * from ${DB.TABLE_CHARTS} where ${DB.CHART_ID} = :chartId")
    suspend fun getAllDataOnChartId(chartId: Long): ChartAllData

    @Transaction
    @Query("select * from ${DB.TABLE_CHARTS}")
    suspend fun getAllDataOnAllCharts(): List<ChartAllData>

    @Transaction
    @Query("select * from ${DB.TABLE_CHARTS}")
    suspend fun getAllDChartsWithLines(): List<ChartAndLines>
}