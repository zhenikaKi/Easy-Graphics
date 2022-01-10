package ru.easygraphics.tableWindow.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.easygraphics.R
import ru.easygraphics.data.domain.TableLineData
import ru.easygraphics.helpers.consts.App.LOG_TAG

class TableAdapterV(
    private val delegate: Delegate?
) : RecyclerView.Adapter<TableViewHolderV>() {

    interface Delegate {
        fun onRowSelectedV(tableLineData: TableLineData)
        fun onDeleteSelectedV(position: Int)
    }

    private lateinit var context: Context

    private val dataLines = mutableListOf<TableLineData>()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(dataToSet: List<TableLineData>) {
        dataLines.apply {
            clear()
            addAll(dataToSet)
            notifyDataSetChanged()
        }
    }

    fun removeItem(position: Int) {
        dataLines.removeAt(position)
        Log.d(LOG_TAG, "Position: $position")
        notifyItemChanged(position - 1)
        notifyItemChanged(position + 1)
        notifyItemRemoved(position)
        //notifyItemRangeChanged(position, itemCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TableViewHolderV {
        val rootView = LayoutInflater.from(parent.context)
            .inflate(R.layout.table_line, parent, false)

        context = parent.context

        /*val holder = TableViewHolder(rootView)
        holder.itemView.click {
            val position = holder.layoutPosition
            val model = dataLines[position]

            //delegate?.onRowSelected(lineData)
        }*/

        return TableViewHolderV(rootView)
    }

    override fun onBindViewHolder(holderV: TableViewHolderV, position: Int) {
        holderV.bind(dataLines[position], context = context, delegate)
    }

    override fun getItemCount(): Int = dataLines.size

    override fun getItemId(position: Int) = position.toLong()

    override fun getItemViewType(position: Int) = position

    interface OnDeleteIconClickListener {
        fun onItemClick()
    }
}