package ru.easygraphics.data.db.entities

import androidx.room.Embedded
import androidx.room.Relation
import ru.easygraphics.helpers.consts.DB

/** общий класс с информацией по графику и линиям графика */
data class ChartAndLines (
    @Embedded
    val chart: Chart,

    @Relation(parentColumn = DB.CHART_ID, entity = ChartLine::class, entityColumn = DB.CHART_ID)
    val lines: List<ChartLineData>
)