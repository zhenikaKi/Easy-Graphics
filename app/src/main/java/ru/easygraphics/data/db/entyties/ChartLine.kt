package ru.easygraphics.data.db.entyties

import androidx.room.*
import ru.easygraphics.helpers.consts.DB

/** Названия линий графика */
@Entity(tableName = DB.TABLE_CHART_LINES,
    indices = [Index(DB.CHART_ID)],
    foreignKeys = [ForeignKey(
        entity = Chart::class,
        parentColumns = [DB.CHART_ID],
        childColumns = [DB.CHART_ID],
        onDelete = ForeignKey.CASCADE
    )]
)
data class ChartLine(
    /** ID линии диаграммы */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = DB.LINE_ID)
    val lineId: Long,

    /** ID диаграммы, к которой относятся линия */
    @ColumnInfo(name = DB.CHART_ID)
    val chartId: Long,

    /** Название линии */
    @ColumnInfo(name = DB.COLUMN_NAME)
    val name: String,

    /** Код цвета линии */
    @ColumnInfo(name = DB.COLOR)
    val color: String
)
