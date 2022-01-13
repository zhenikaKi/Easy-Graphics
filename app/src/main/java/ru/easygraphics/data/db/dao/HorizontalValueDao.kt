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

    //обновить значение Х в таблице
    @Query("update ${DB.TABLE_HORIZONTAL_VALUE} set value = :xValue where ${DB.CHART_ID} = :chartId and ${DB.X_VALUE_ID} = :xValueId")
    suspend fun updateValue(
        chartId: Long,
        xValueId: Long?,
        xValue: String
    )

    //удалить значение Х в таблицы
    @Query("delete from ${DB.TABLE_HORIZONTAL_VALUE} where ${DB.X_VALUE_ID} in (:xValuesId)")
    suspend fun deleteById(xValuesId: List<Long>)

    @Update
    suspend fun update(horizontalValue: List<HorizontalValue>)
}