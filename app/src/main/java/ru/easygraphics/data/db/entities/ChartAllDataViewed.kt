package ru.easygraphics.data.db.entities

data class ChartAllDataViewed(
    //информация по графику
    var chart: Chart,

    //информация по всем линиям
    var lines: List<ChartLine> = listOf(),

    //все значения по всем линиям
    var values: List<HorizontalValueWithLinesValue> = listOf()
)