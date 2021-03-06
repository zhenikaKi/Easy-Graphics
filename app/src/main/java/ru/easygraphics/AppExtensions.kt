package ru.easygraphics

import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import ru.easygraphics.data.db.entities.ChartAllData
import ru.easygraphics.data.db.entities.ChartAllDataViewed
import ru.easygraphics.data.db.entities.ChartLineData
import ru.easygraphics.data.domain.LineDetails
import ru.easygraphics.data.domain.TableLineData
import ru.easygraphics.helpers.consts.App.LOG_TAG
import kotlin.math.max

fun View.visibleOrGone(isVisible: Boolean): View {
    visibility = if (isVisible) View.VISIBLE else View.GONE
    return this
}

fun View.click(click: () -> Unit) = setOnClickListener {
    click()
}

fun View.longClick(click: () -> Unit) = setOnLongClickListener {
    click()
    true
}

fun Fragment.arguments(vararg arguments: Pair<String, Any>): Fragment {
    this.arguments = bundleOf(*arguments)
    return this
}

fun TextView.addBottomBorder(width: Int) {
    val bottomBorder: LayerDrawable = getBorders(Color.GRAY, 0, 0, 0, width)
    background = bottomBorder
}

fun TextView.addRightBorder(width: Int) {
    background = getBorders(Color.GRAY, 0, 0, width, 0)
}

fun TextView.addRightBottomBorder(width: Int) {
    background = getBorders(Color.GRAY, 0, 0, width, width)
}

private fun getBorders(
    borderColor: Int,
    left: Int,
    top: Int,
    right: Int,
    bottom: Int
): LayerDrawable {

    val borderColorDrawable = ColorDrawable(borderColor)
    val backgroundColorDrawable = ColorDrawable(Color.WHITE)
    val drawables = arrayOf<Drawable>(
        borderColorDrawable,
        backgroundColorDrawable
    )

    val layerDrawable = LayerDrawable(drawables)

    layerDrawable.setLayerInset(1, left, top, right, bottom)
    return layerDrawable
}

fun TextView.addBorders(
    color: Int = Color.GRAY,
    width: Float = 10F
) {
    val drawable = ShapeDrawable().apply {
        shape = RectShape()
        paint.apply {
            this.color = color
            strokeWidth = width
            style = Paint.Style.STROKE
        }
    }
    background = drawable
}

fun ChartAllDataViewed.parseToListOfTableLineData(): List<TableLineData> {
    val result = mutableListOf<TableLineData>()

    val firstRow = mutableListOf<Pair<String, Int>>()

    var firstRowWidth = this.chart.xName.length
    this.values.forEach {
        firstRowWidth = max(firstRowWidth, it.horizontalValue.value.length)
    }

    firstRow.add(Pair(this.chart.xName, firstRowWidth))
    Log.d(LOG_TAG, "$this")

    this.lines.forEach { chartLine ->
        var maxLength = chartLine.name.length

        this.values.forEach {
            val verticalMaxLength = it.verticalValues.firstOrNull { verticalValue ->
                verticalValue?.lineId == chartLine.lineId
            }?.value.toString().length

            maxLength = max(maxLength, verticalMaxLength)
        }

        firstRow.add(Pair(chartLine.name, maxLength))
    }
    return result
}

private fun columnWidthCalculation(chartLineData: ChartLineData): Int {

    var maxLength = chartLineData.chartLine.name.length

    chartLineData.yValues.forEach { verticalValue ->
        maxLength = max(maxLength, verticalValue.value.toString().length)
    }

    maxLength = max(maxLength, ExtConst.MinColumnWidth)

    return maxLength
}

fun ChartAllData.parseToListOfTableLineData(): List<TableLineData> {

    val result = mutableListOf<TableLineData>()

    val firstRow = mutableListOf<LineDetails>()
    firstRow.add(LineDetails(this.chart.xName, this.chart.xName.length, true))

    this.lines.forEach {
        firstRow.add(LineDetails(it.chartLine.name, columnWidthCalculation(it), true))
    }

    val firstRowData = TableLineData(
        dbId = -1,
        lineId = -1,
        lineName = this.chart.xName,
        lineValue = firstRow
    )

    Log.d(LOG_TAG, "$firstRowData")
    result.add(firstRowData)

    for ((lineId, i) in this.xValues.indices.withIndex()) {
        val newRowData = TableLineData(
            dbId = this.xValues[i].xValueId,
            lineId = lineId,
            lineName = this.xValues[i].value,
            lineValue = mutableListOf<LineDetails>()
        )
        newRowData.addLineValue(newRowData.lineName, firstRowData.lineValue[0].width, false)

        for (l in this.lines.indices) {
            val yValue = this.lines[l].yValues[i].value.toString()
            newRowData.addLineValue(yValue, firstRowData.lineValue[l + 1].width, false)
        }

        Log.d(LOG_TAG, "$newRowData")
        if (newRowData.lineValue.size > 0) {
            result.add(newRowData)
        }
    }

    return result
}

object ExtConst {
    const val MinColumnWidth = 5
}