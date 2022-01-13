package ru.easygraphics.data.db.dao

import androidx.room.*
import ru.easygraphics.data.db.entities.Chart
import ru.easygraphics.data.db.entities.VerticalValue
import ru.easygraphics.helpers.consts.DB

@Dao
interface VerticalValueDao {

    //сохранить значение по оси Y
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(verticalValue: List<VerticalValue>)

    //удалить значение по оси Y
    @Delete
    suspend fun delete(verticalValue: VerticalValue)

    //получить все значения по оси Y для конкретной линии
    @Query("select * from ${DB.TABLE_VERTICAL_VALUE} where ${DB.LINE_ID} = :lineId")
    suspend fun getValues(lineId: Long): List<VerticalValue>

    //обновить значения Y в таблицы
    @Query("update ${DB.TABLE_VERTICAL_VALUE} set value = :yValue where ${DB.LINE_ID} = :lineId and ${DB.X_VALUE_ID} = :xValueId and ${DB.Y_VALUE_ID} = :yValueId")
    suspend fun updateValue(
        lineId: Long,
        xValueId: Long,
        yValueId: Long?,
        yValue: Double?
    )

    @Update
    suspend fun update(verticalValue: List<VerticalValue>)
}