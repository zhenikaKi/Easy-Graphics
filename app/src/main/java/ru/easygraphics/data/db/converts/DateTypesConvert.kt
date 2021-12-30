package ru.easygraphics.data.db.converts

import androidx.room.TypeConverter
import ru.easygraphics.helpers.consts.DB

/** Конвертиование перечисления DB.DateTypes в Int и наоборот для хранения в БД */
class DateTypesConvert {
    @TypeConverter
    fun valueToEnum(value: Int?): DB.DateTypes? = DB.DateTypes.values().find { it.value == value }

    @TypeConverter
    fun enumToValue(enumValue: DB.DateTypes?): Int? = enumValue?.value
}