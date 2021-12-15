package ru.easygraphics.data.db.converts

import androidx.room.TypeConverter
import ru.easygraphics.helpers.consts.DB

/** Конвертиование перечисления DB.ValueTypes в Int и наоборот для хранения в БД */
class ValueTypesConvert {
    @TypeConverter
    fun valueToEnum(value: Int): DB.ValueTypes =
        DB.ValueTypes.values().find { it.value == value } ?: DB.ValueTypes.STRING

    @TypeConverter
    fun enumToValue(enumValue: DB.ValueTypes): Int = enumValue.value
}