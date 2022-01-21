package ru.easygraphics.graphicwindow

import com.github.mikephil.charting.data.LineData
import ru.easygraphics.data.db.entities.Chart
import ru.easygraphics.helpers.XValueFormatter

data class ChartGraphicDto(
    val chart: Chart,
    val xValueFormatter: XValueFormatter,
    val lineData: LineData,
    val zoomScale: Float
)
