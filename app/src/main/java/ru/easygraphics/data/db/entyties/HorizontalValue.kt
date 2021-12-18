package ru.easygraphics.data.db.entyties

import androidx.room.*
import ru.easygraphics.helpers.consts.DB

/** Значение по оси X */
@Entity(tableName = DB.TABLE_HORIZONTAL_VALUE,
    indices = [Index(DB.CHART_ID)],
    foreignKeys = [ForeignKey(
        entity = Chart::class,
        parentColumns = [DB.CHART_ID],
        childColumns = [DB.CHART_ID],
        onDelete = ForeignKey.CASCADE
    )]
)
data class HorizontalValue(
    /** ID значения подписи */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = DB.X_VALUE_ID)
    val xValueId: Long?,

    /** ID диаграммы, к которой относятся значение подписи */
    @ColumnInfo(name = DB.CHART_ID)
    val chartId: Long,

    /** Значение подписи */
    @ColumnInfo(name = DB.VALUE)
    val value: String
)
