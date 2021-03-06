package ru.easygraphics.data.db.dao

import androidx.room.*
import ru.easygraphics.data.db.entities.Chart
import ru.easygraphics.helpers.consts.DB

@Dao
interface ChartDao {

    //сохранить график
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(chart: Chart): Long

    //обновить график
    @Update
    suspend fun update(chart: Chart)

    //удалить график
    @Delete
    suspend fun delete(chart: Chart)

    //удалить график по его id
    @Query("delete from ${DB.TABLE_CHARTS} where ${DB.CHART_ID} = :chartId")
    suspend fun delete(chartId: Long)

    //получить конкретный график
    @Query("select * from ${DB.TABLE_CHARTS} where ${DB.CHART_ID} = :chartId")
    suspend fun getChart(chartId: Long): Chart

    //получить все графики
    @Query("select * from ${DB.TABLE_CHARTS}")
    suspend fun getCharts(): List<Chart>
}