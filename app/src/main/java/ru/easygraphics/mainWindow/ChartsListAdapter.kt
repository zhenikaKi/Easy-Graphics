package ru.easygraphics.mainWindow

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_list_charts.view.*
import ru.easygraphics.R

class ChartsListAdapter(
    val onChartClickListener: OnChartClickListener,
    val onChartLongClickListener: OnChartLongClickListener
) : RecyclerView.Adapter<ChartsListAdapter.ViewHolder>() {
    private var chartsList: ArrayList<Pair<Long, String>> = arrayListOf()
    fun setData(l: List<Pair<Long, String>>) {
        chartsList.clear()
        chartsList.addAll(l)
        notifyDataSetChanged()
    }

    fun removeItem(pos: Int) {
        chartsList.removeAt(pos)
        notifyItemRemoved(pos)
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(p0.context).inflate(R.layout.item_list_charts, p0, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.bind(chartsList[p1], onChartClickListener, onChartLongClickListener)
    }

    override fun getItemCount(): Int {
        return chartsList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(p: Pair<Long, String>, cl: OnChartClickListener, lcl: OnChartLongClickListener) {
            itemView.textView2.setText(p.second + "\n" + "id:" + p.first)
            itemView.setOnClickListener {
                cl.onChartClick(p.first)
            }
            itemView.setOnLongClickListener {
                lcl.onChartLongClick(p.first, layoutPosition, it)
                true
            }
        }
    }

    interface OnChartClickListener {
        fun onChartClick(chartId: Long)
    }

    interface OnChartLongClickListener {
        fun onChartLongClick(chartId: Long, pos: Int, view: View)
    }
}