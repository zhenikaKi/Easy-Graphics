package ru.easygraphics.settingwindow

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class SettingAdapter(private val items: List<SettingItemType>,
                     private val listener: SettingAdapterListener):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return FactorySettingViewHolder.create(parent, viewType, listener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as SettingViewHolder).setData(items[position])
    }

    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int) = items[position].getType()
}