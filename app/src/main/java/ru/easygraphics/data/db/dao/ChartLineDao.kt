package ru.easygraphics.data.db.dao

import androidx.room.*
import ru.easygraphics.data.db.entities.ChartLine
import ru.easygraphics.helpers.consts.DB

@Dao
interface ChartLineDao {

    //сохранить линию графика
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(chartLine: ChartLine)

    //удалить линию графика
    @Delete
    suspend fun delete(chartLine: ChartLine)

    //получить все линии графика
    @Query("select * from ${DB.TABLE_CHART_LINES} where ${DB.CHART_ID} = :chartId")
    suspend fun getLines(chartId: Long): List<ChartLine>
}