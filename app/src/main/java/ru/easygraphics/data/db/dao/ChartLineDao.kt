package ru.easygraphics.data.db.dao

import androidx.room.*
import ru.easygraphics.data.db.entities.ChartLine
import ru.easygraphics.helpers.consts.DB

@Dao
interface ChartLineDao {

    //сохранить линию графика
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(lines: ChartLine): Long

    //сохранить линии графика
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(lines: List<ChartLine>): List<Long>

    //обновить линию графика
    @Update
    suspend fun update(line: ChartLine)

    //обновить линии графика
    @Update
    suspend fun update(lines: List<ChartLine>)

    //удалить линию графика
    @Delete
    suspend fun delete(chartLine: ChartLine)

    //удалить линии графика
    @Query("delete from ${DB.TABLE_CHART_LINES} where ${DB.LINE_ID} in (:chartLinesId)")
    suspend fun delete(chartLinesId: List<Long>)

    //получить все линии графика
    @Query("select * from ${DB.TABLE_CHART_LINES} where ${DB.CHART_ID} = :chartId")
    suspend fun getLines(chartId: Long): List<ChartLine>
}