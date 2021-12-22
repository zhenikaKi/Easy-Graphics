package ru.easygraphics.data.db.entities

import androidx.room.*
import ru.easygraphics.helpers.consts.DB

/** Значение по оси X */
@Entity(tableName = DB.TABLE_VERTICAL_VALUE,
    indices = [Index(DB.LINE_ID), Index(DB.X_VALUE_ID)],
    foreignKeys = [
        ForeignKey(
            entity = ChartLine::class,
            parentColumns = [DB.LINE_ID],
            childColumns = [DB.LINE_ID],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = HorizontalValue::class,
            parentColumns = [DB.X_VALUE_ID],
            childColumns = [DB.X_VALUE_ID],
            onDelete = ForeignKey.CASCADE
        )]
)
data class VerticalValue(
    /** ID значения */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = DB.Y_VALUE_ID)
    val yValueId: Long?,

    /** ID линии диаграммы, к которой относятся значение */
    @ColumnInfo(name = DB.LINE_ID)
    val lineId: Long,

    /** ID подписи по оси X */
    @ColumnInfo(name = DB.X_VALUE_ID)
    val xValueId: Long,

    /** Значение */
    @ColumnInfo(name = DB.VALUE)
    val value: Double?
)
