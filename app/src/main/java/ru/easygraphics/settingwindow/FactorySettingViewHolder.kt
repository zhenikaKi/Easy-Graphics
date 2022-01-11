package ru.easygraphics.settingwindow

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.easygraphics.databinding.ItemMainSettingBinding

/** Построитель ViewHolder'ов для адаптера настроек */
object FactorySettingViewHolder {
    fun create(parent: ViewGroup, viewType: Int, listener: SettingAdapterListener): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            //экспорт или импорт
            SettingItemType.EXPORT_TYPE, SettingItemType.IMPORT_TYPE -> {
                val binding = ItemMainSettingBinding.inflate(inflater, parent, false)
                MainViewHolder(binding, listener)
            }

            //элемент настроек по умолчанию
            else -> {
                val binding = ItemMainSettingBinding.inflate(inflater, parent, false)
                MainViewHolder(binding, listener)
            }
        }
    }

    /** Сформировать стандартный viewHolder, содержащий заголовок и описание. */
    private class MainViewHolder(private val binding: ItemMainSettingBinding,
                                 private val listener: SettingAdapterListener):
        RecyclerView.ViewHolder(binding.root), SettingViewHolder {

        override fun setData(itemData: SettingItemType) {
            if (itemData is SettingMainItem) {
                binding.itemTitle.text = itemData.title
                binding.itemDescription.text = itemData.description

                //обработка нажатия по элементу
                binding.root.setOnClickListener {
                    when (itemData.getType()) {
                        SettingItemType.EXPORT_TYPE -> listener.exportGraphics()
                        SettingItemType.IMPORT_TYPE -> listener.importGraphics()
                    }
                }
            }
        }

    }
}