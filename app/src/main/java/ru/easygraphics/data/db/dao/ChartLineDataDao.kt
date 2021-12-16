package ru.easygraphics.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import ru.easygraphics.data.db.entyties.ChartLineData
import ru.easygraphics.helpers.consts.DB

@Dao
interface ChartLineDataDao {
    @Transaction
    @Query("select * from ${DB.TABLE_CHART_LINES} where ${DB.CHART_ID} = :chartId")
    suspend fun getLinesOnChartId(chartId: Long): List<ChartLineData>
}