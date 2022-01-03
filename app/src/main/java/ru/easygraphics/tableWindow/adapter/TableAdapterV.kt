package ru.easygraphics.tableWindow.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.easygraphics.R
import ru.easygraphics.click
import ru.easygraphics.data.domain.TableLineData
import ru.easygraphics.helpers.consts.App.LOG_TAG

class TableAdapterV(
    private val delegate: Delegate?
) : RecyclerView.Adapter<TableViewHolder>() {

    interface Delegate {
        fun onRowSelected(tableLineData: TableLineData)
        fun onDeleteSelected(position: Int)
    }

    private lateinit var context: Context

    private val dataLines = mutableListOf<TableLineData>()

    fun setData(dataToSet: List<TableLineData>) {
        dataLines.apply {
            clear()
            addAll(dataToSet)
        }
    }

    fun removeItem(position: Int) {
        dataLines.removeAt(position)
        Log.d(LOG_TAG, "Position: $position")
        notifyItemChanged(position - 1)
        notifyItemChanged(position + 1)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TableViewHolder {
        val rootView = LayoutInflater.from(parent.context)
            .inflate(R.layout.table_line, parent, false)

        context = parent.context

        /*val holder = TableViewHolder(rootView)
        holder.itemView.click {
            val position = holder.layoutPosition
            val model = dataLines[position]

            //delegate?.onRowSelected(lineData)
        }*/

        return TableViewHolder(rootView)
    }

    override fun onBindViewHolder(holder: TableViewHolder, position: Int) =
        holder.bind(dataLines[position], context = context, delegate)

    override fun getItemCount(): Int = dataLines.size

    override fun getItemId(position: Int) = position.toLong()

    override fun getItemViewType(position: Int) = position

    interface OnDeleteIconClickListener {
        fun onItemClick()
    }
}