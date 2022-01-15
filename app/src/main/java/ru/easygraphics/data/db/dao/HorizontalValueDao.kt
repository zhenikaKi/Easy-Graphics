package ru.easygraphics.data.db.dao

import androidx.room.*
import ru.easygraphics.data.db.entities.HorizontalValue
import ru.easygraphics.data.db.entities.VerticalValue
import ru.easygraphics.helpers.consts.DB

@Dao
interface HorizontalValueDao {

    //сохранить значение по оси X
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(horizontalValue: HorizontalValue): Long

    //удалить значение по оси X
    @Delete
    suspend fun delete(horizontalValue: HorizontalValue)

    //получить все значения по оси X на конкретном графике
    @Query("select * from ${DB.TABLE_HORIZONTAL_VALUE} where ${DB.CHART_ID} = :chartId")
    suspend fun getValues(chartId: Long): List<HorizontalValue>

    //удалить значение Х в таблицы
    @Query("delete from ${DB.TABLE_HORIZONTAL_VALUE} where ${DB.X_VALUE_ID} in (:xValuesId)")
    suspend fun deleteById(xValuesId: List<Long>)

    //обновить значение Х в таблице
    @Update
    suspend fun update(horizontalValue: HorizontalValue)
}