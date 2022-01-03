package ru.easygraphics.tableWindow.adapter

import android.content.Context
import android.graphics.Typeface
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.easygraphics.R
import ru.easygraphics.addRightBorder
import ru.easygraphics.addRightBottomBorder
import ru.easygraphics.click
import ru.easygraphics.data.domain.LineDetails
import ru.easygraphics.data.domain.TableLineData


class TableViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private lateinit var holderContext: Context
    private var holderDelegate: TableAdapterV.Delegate? = null

    fun bind(lineData: TableLineData, context: Context, delegate: TableAdapterV.Delegate?) {
        holderContext = context
        holderDelegate = delegate
        val layout: LinearLayout = itemView.findViewById(R.id.table_line)

        var isFirstItem = true
        lineData.LineValue.forEach { lineDetails ->
            if (isFirstItem) {
                val newImageView = createImageView(lineDetails.IsHead)
                layout.addView(newImageView)
                newImageView.click {
                    holderDelegate?.onDeleteSelected(layoutPosition)
                }
            }
            layout.addView(createTextView(lineDetails))
            isFirstItem = false
        }

        layout.click {
            delegate?.onRowSelected(lineData)
        }
    }

    private fun createImageView(isHead: Boolean): ImageView {
        val newImageView = ImageView(holderContext)

        newImageView.setImageResource(R.drawable.ic_delete)
        newImageView.y = holderContext.resources.getDimension(R.dimen.default_card_text_size) / 3
        if (isHead) newImageView.visibility = View.INVISIBLE

        return newImageView
    }

    private fun createTextView(lineDetails: LineDetails): TextView {
        val newTextView = TextView(holderContext)
        val defaultCardTextSize =
            holderContext.resources.getDimension(R.dimen.default_card_text_size)

        newTextView.text = lineDetails.Value
        newTextView.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            defaultCardTextSize
        )
        newTextView.addRightBorder(5)
        if (lineDetails.IsHead) {
            newTextView.addRightBottomBorder(6)
            newTextView.typeface = Typeface.DEFAULT_BOLD
        }

        newTextView.gravity = Gravity.CENTER
        newTextView.setTextColor(holderContext.resources.getColor(R.color.black))
        newTextView.height = defaultCardTextSize.toInt() * 2
        newTextView.width = defaultCardTextSize.toInt() * lineDetails.Width

        return newTextView
    }
}