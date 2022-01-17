package ru.easygraphics.mainWindow

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_main_setting.view.*
import ru.easygraphics.R
import ru.easygraphics.data.db.entities.ChartAndLines

class ChartsListAdapter(
    private val onChartClickListener: OnChartClickListener,
    private val onChartLongClickListener: OnChartLongClickListener
) : RecyclerView.Adapter<ChartsListAdapter.ViewHolder>() {
    private var chartsList: ArrayList<ChartAndLines> = arrayListOf()
    fun setData(l: List<ChartAndLines>) {
        chartsList.clear()
        chartsList.addAll(l)
        notifyDataSetChanged()
    }

    fun removeItem(pos: Int) {
        chartsList.removeAt(pos)
        notifyItemRemoved(pos)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_main_setting, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(chartsList[position], onChartClickListener, onChartLongClickListener)
    }

    override fun getItemCount(): Int {
        return chartsList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(data: ChartAndLines, onChartClickListener: OnChartClickListener, onChartLongClickListener: OnChartLongClickListener) {
            itemView.item_title.text = data.chart.name
            itemView.item_description.text = data.lines.map { line -> line.chartLine.name }.toString()
            itemView.setOnClickListener {
                onChartClickListener.onChartClick(data.chart.chartId!!)
            }
            itemView.setOnLongClickListener {
                onChartLongClickListener.onChartLongClick(data.chart.chartId!!, layoutPosition, it)
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