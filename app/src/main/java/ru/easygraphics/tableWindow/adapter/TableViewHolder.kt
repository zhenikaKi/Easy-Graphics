package ru.easygraphics.tableWindow.adapter

import android.content.Context
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.easygraphics.R
import ru.easygraphics.addRightBorder
import ru.easygraphics.click
import ru.easygraphics.data.domain.TableLineData


class TableViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(lineData: TableLineData, context: Context, delegate: TableAdapter.Delegate?) {
        val layout: LinearLayout = itemView.findViewById(R.id.table_line)
        val defaultCardTextSize = context.resources.getDimension(R.dimen.default_card_text_size)

        lineData.LineValue.forEach { value ->

            val newTextView = TextView(context)
            newTextView.text = value.first
            newTextView.setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                defaultCardTextSize
            )
            //newTextView.addBottomBorder(5)
            newTextView.addRightBorder(5)
            newTextView.gravity = Gravity.CENTER

            newTextView.setTextColor(context.resources.getColor(R.color.black))
            newTextView.width = defaultCardTextSize.toInt() * value.second
            layout.addView(newTextView)
        }

        layout.click {
            delegate?.onRowSelected(lineData)
        }
    }
}