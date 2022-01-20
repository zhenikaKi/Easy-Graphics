package ru.easygraphics.mainWindow

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_list_charts.view.*
import kotlinx.android.synthetic.main.item_main_setting.view.*
import kotlinx.android.synthetic.main.item_main_setting.view.item_description
import kotlinx.android.synthetic.main.item_main_setting.view.item_title
import ru.easygraphics.R
import ru.easygraphics.data.db.entities.ChartAndLines

class ChartsListAdapter(
    private val onChartClickListener: OnChartClickListener
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
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list_charts, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(chartsList[position],onChartClickListener)
    }

    override fun getItemCount(): Int {
        return chartsList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(data: ChartAndLines, onChartClickListener: OnChartClickListener) {
            itemView.item_title.text = data.chart.name
            itemView.item_description.text = data.lines.map { line -> line.chartLine.name }.toString()
            data.chart.chartId?.let {chartId->
                itemView.icon_graphic.setOnClickListener {
                    onChartClickListener.onIconGraphicClick(chartId)
                }
                itemView.icon_table.setOnClickListener {
                    onChartClickListener.onIconTableClick(chartId)
                }
                itemView.icon_edit.setOnClickListener {
                    onChartClickListener.onIconEditClick(chartId)
                }
                itemView.icon_delete.setOnClickListener {
                    onChartClickListener.onIconDeleteClick(chartId, layoutPosition)
                }
            }
        }
    }

    interface OnChartClickListener {
        fun onIconGraphicClick(chartId: Long)
        fun onIconTableClick(chartId: Long)
        fun onIconEditClick(chartId: Long)
        fun onIconDeleteClick(chartId: Long, pos: Int)
    }
}