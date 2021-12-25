package ru.easygraphics

import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import ru.easygraphics.data.db.entities.ChartAllData
import ru.easygraphics.data.domain.TableLineData
import ru.easygraphics.helpers.consts.App.LOG_TAG

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