package ru.easygraphics

import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import ru.easygraphics.data.db.entities.ChartAllData
import ru.easygraphics.data.db.entities.ChartAllDataViewed
import ru.easygraphics.data.domain.TableLineData
import ru.easygraphics.helpers.consts.App.LOG_TAG

fun View.click(click: () -> Unit) = setOnClickListener {
    click()
}

fun Fragment.arguments(vararg arguments: Pair<String, Any>): Fragment {
    this.arguments = bundleOf(*arguments)
    return this
}

fun Fragment.toast(string: String?) {
    Toast.makeText(context, string, Toast.LENGTH_SHORT).apply {
        setGravity(Gravity.BOTTOM, 0, 250)
        show()
    }
}

fun TextView.addBottomBorder(width: Int) {
    val bottomBorder: LayerDrawable = getBorders(Color.GRAY,0, 0, 0, width)
    background = bottomBorder
}

fun TextView.addRightBorder(width: Int) {
    val bottomBorder: LayerDrawable = getBorders(Color.GRAY,0,0, width,0)
    background = bottomBorder
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
        firstRowWidth =
            if (firstRowWidth < it.horizontalValue.value.length) it.horizontalValue.value.length else firstRowWidth
    }

    firstRow.add(Pair(this.chart.xName, firstRowWidth))
    Log.d(LOG_TAG, "$this")

    this.lines.forEach { chartLine ->
        var maxLength = chartLine.name.length

        this.values.forEach {
            val verticalMaxLength = it.verticalValues.firstOrNull { verticalValue ->
                verticalValue?.lineId == chartLine.lineId
            }?.value.toString().length

            if (maxLength < verticalMaxLength) {
                maxLength = verticalMaxLength
            }
        }

        firstRow.add(Pair(chartLine.name, maxLength))
    }

    val firstRowData = TableLineData(
        DbId = -1,
        LineId = -1,
        LineName = this.chart.xName,
        LineValue = firstRow
    )

    Log.d(LOG_TAG, "$firstRowData")
    result.add(firstRowData)

    this.values.forEach { horizontalValue ->
        val newRowData = horizontalValue.horizontalValue.xValueId?.let { xValueId ->
            TableLineData(
                DbId = -1,
                LineId = xValueId.toInt(),
                LineName = horizontalValue.horizontalValue.value,
                LineValue = mutableListOf<Pair<String, Int>>()
            )
        }

        newRowData?.let { tableLineData ->

            /*val width: Int = firstRowData.LineValue.first {
                it.first == horizontValue.horizontalValue.value
            }.second*/

            tableLineData.addLineValue(horizontalValue.horizontalValue.value, firstRowWidth)

            horizontalValue.verticalValues.forEach { verticalValue ->
                tableLineData.addLineValue(verticalValue?.value.toString(), 5)
            }

            Log.d(LOG_TAG, "$newRowData")
            result.add(newRowData)
        }
    }

    return result
}

fun ChartAllData.parseToListOfTableLineData(): List<TableLineData> {

    val result = mutableListOf<TableLineData>()

    val firstRow = mutableListOf<Pair<String, Int>>()
    firstRow.add(Pair(this.chart.xName, this.chart.xName.length))

    this.lines.forEach {
        var maxLength = it.chartLine.name.length

        it.yValues.forEach { verticalValue ->
            maxLength =
                if (maxLength < verticalValue.value.toString().length) verticalValue.value.toString().length else maxLength
        }
        firstRow.add(Pair(it.chartLine.name, maxLength))
    }

    val firstRowData = TableLineData(
        DbId = -1,
        LineId = -1,
        LineName = this.chart.xName,
        LineValue = firstRow
    )

    Log.d(LOG_TAG, "$firstRowData")
    result.add(firstRowData)

    for ((lineId, i) in this.xValues.indices.withIndex()) {
        val newRowData = TableLineData(
            DbId = this.xValues[i].xValueId,
            LineId = lineId,
            LineName = this.xValues[i].value,
            LineValue = mutableListOf<Pair<String, Int>>()
        )
        newRowData.addLineValue(newRowData.LineName, firstRowData.LineValue[0].second)

        for (l in this.lines.indices) {
            val yValue = this.lines[l].yValues[i].value.toString()
            newRowData.addLineValue(yValue, firstRowData.LineValue[l + 1].second)
        }

        Log.d(LOG_TAG, "$newRowData")
        if (newRowData.LineValue.size > 0) {
            result.add(newRowData)
        }
    }

    return result
}