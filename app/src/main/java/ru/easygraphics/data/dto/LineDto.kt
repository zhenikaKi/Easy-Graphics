package ru.easygraphics.data.dto

/** Вспомогательная сущность линий для экспорта и импорта графиков */
data class LineDto(
    val title: String,
    val color: String,
    val yValues: List<Double?>
)