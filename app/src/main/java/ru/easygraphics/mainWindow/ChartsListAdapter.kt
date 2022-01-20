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
    private val onIconGraphicClickListener: OnIconGraphicClickListener,
    private val onIconTableClickListener: OnIconTableClickListener,
    private val onIconEditClickListener:OnIconEditClickListener,
    private val onIconDeleteClickListener:OnIconDeleteClickListener
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
        holder.bind(chartsList[position], onIconGraphicClickListener, onIconTableClickListener,onIconEditClickListener,onIconDeleteClickListener)
    }

    override fun getItemCount(): Int {
        return chartsList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(data: ChartAndLines, onIconGraphicClickListener: OnIconGraphicClickListener, onIconTableClickListener: OnIconTableClickListener,onIconEditClickListener: OnIconEditClickListener,onIconDeleteClickListener: OnIconDeleteClickListener) {
            itemView.item_title.text = data.chart.name
            itemView.item_description.text = data.lines.map { line -> line.chartLine.name }.toString()
            itemView.icon_graphic.setOnClickListener{
                onIconGraphicClickListener.onIconGraphicClick(data.chart.chartId!!)
            }
            itemView.icon_table.setOnClickListener{
                onIconTableClickListener.onIconTableClick(data.chart.chartId!!)
            }
            itemView.icon_edit.setOnClickListener{
                onIconEditClickListener.onIconEditClick(data.chart.chartId!!)
            }
            itemView.icon_delete.setOnClickListener{
                onIconDeleteClickListener.onIconDeleteClick(data.chart.chartId!!,layoutPosition)
            }
        }
    }

    interface OnIconGraphicClickListener {
        fun onIconGraphicClick(chartId: Long)
    }
    interface OnIconTableClickListener {
        fun onIconTableClick(chartId: Long)
    }
    interface OnIconEditClickListener {
        fun onIconEditClick(chartId: Long)
    }
    interface OnIconDeleteClickListener {
        fun onIconDeleteClick(chartId: Long, pos: Int)
    }
}