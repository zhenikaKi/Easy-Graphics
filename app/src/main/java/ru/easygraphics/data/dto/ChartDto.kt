package ru.easygraphics.data.dto

import java.math.BigDecimal

/** Вспомогательная сущность для графика при экспорте и иморте данных */
data class ChartDto(
    val title: String,
    val countDecimal: Int = 2,
    val xValueType: Int = 1,
    val xValueDateFormat: Int? = null,
    val xName: String = "X",
    val yName: String = "Y",
    val xValues: List<String>,
    val lines: List<LineDto>
)
