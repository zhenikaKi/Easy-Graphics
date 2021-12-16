package ru.easygraphics.data.db.entyties

import androidx.room.Embedded
import androidx.room.Relation
import ru.easygraphics.helpers.consts.DB

/** общий класс с данными по всему графику */
data class ChartAllData (
    @Embedded
    val chart: Chart,

    @Relation(parentColumn = DB.CHART_ID, entity = ChartLine::class, entityColumn = DB.CHART_ID)
    val lines: List<ChartLineData>,

    @Relation(parentColumn = DB.CHART_ID, entity = HorizontalValue::class, entityColumn = DB.CHART_ID)
    val xValues: List<HorizontalValue>
)