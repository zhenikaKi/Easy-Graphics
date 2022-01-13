package ru.easygraphics

import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.provider.ContactsContract
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import io.github.ekiryushin.scrolltableview.cell.CellView
import io.github.ekiryushin.scrolltableview.cell.DataStatus
import io.github.ekiryushin.scrolltableview.cell.RowCell
import ru.easygraphics.data.db.entities.*
import ru.easygraphics.data.domain.LineDetails
import ru.easygraphics.data.domain.TableLineData
import ru.easygraphics.helpers.consts.App.LOG_TAG
import kotlin.math.max

fun View.click(click: () -> Unit) = setOnClickListener {
    click()
}

fun View.longClick(click: () -> Unit) = setOnLongClickListener {
    click()
    true
}

fun RowCell.toHorizontalValue(chartId: Long): List<HorizontalValue> {
    val result = mutableListOf<HorizontalValue>()

    val xValues = this.columns.filter { cell ->
        cell.viewed != CellView.EDIT_NUMBER && cell.status == DataStatus.EDIT
    }

    xValues.forEach { cell ->
        result.add(
            HorizontalValue(
                xValueId = cell.id,
                chartId = chartId,
                value = cell.value as String
            )
        )
    }

    return result
}

fun RowCell.toVerticalValue(): List<VerticalValue> {
    val result = mutableListOf<VerticalValue>()
    val xValueId = this.columns.firstOrNull { cell ->
        cell.viewed != CellView.EDIT_NUMBER
    }?.id

    val yValues = this.columns.filter { cell ->
        cell.viewed == CellView.EDIT_NUMBER
    }

    for (i in yValues.indices) {
        if (yValues[i].status == DataStatus.EDIT) {
            result.add(
                VerticalValue(
                    yValueId = yValues[i].id,
                    lineId = i + 1.toLong(),
                    xValueId = xValueId as Long,
                    value = yValues[i].value?.toDouble()
                )
            )
        }
    }

    return result
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

    /*val columns: MutableList<Cell> = mutableListOf()
    columns.add(Cell(id = 0, value = graphicData.chart.xName))
    columns.addAll(graphicData.lines.map { line -> Cell(id = line.lineId, value = line.name) })
    val header = RowCell(columns)

    //сформируем основные данные для отображения
    val data: MutableList<RowCell> = graphicData.values.map { hV ->
        val columnsData: MutableList<Cell> = mutableListOf()
        //добавляем колонку со значением по X
        columnsData.add(Cell(id = hV.horizontalValue.xValueId, value = hV.horizontalValue.value, viewed = CellView.EDIT_STRING))
        //добавляем колонки со значениями по Y на каждой линии
        columnsData.addAll(hV.verticalValues.map { vV ->
            Cell(id = vV?.yValueId, value = vV?.value.toString(), viewed = CellView.EDIT_NUMBER)
        })
        RowCell(columnsData)
    }.toMutableList()*/

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

    /*val firstRowData = TableLineData(
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

            tableLineData.addLineValue(horizontalValue.horizontalValue.value, firstRowWidth)

            horizontalValue.verticalValues.forEach { verticalValue ->
                tableLineData.addLineValue(verticalValue?.value.toString(), 5)
            }

            Log.d(LOG_TAG, "$newRowData")
            result.add(newRowData)
        }
    }*/

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