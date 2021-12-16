package ru.easygraphics.data.db.entyties

import androidx.room.Embedded
import androidx.room.Relation
import ru.easygraphics.helpers.consts.DB

/** общий класс с данными по каждой линии */
data class ChartLineData (
    @Embedded
    val chartLine: ChartLine,

    @Relation(parentColumn = DB.LINE_ID, entity = VerticalValue::class, entityColumn = DB.LINE_ID)
    val yValues: List<VerticalValue>
)