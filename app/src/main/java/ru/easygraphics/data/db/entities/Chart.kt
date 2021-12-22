package ru.easygraphics.data.db.entities

import androidx.room.*
import ru.easygraphics.helpers.consts.DB

/** Таблица хранения графика */
@Entity(tableName = DB.TABLE_CHARTS)
data class Chart(
    /** ID диаграммы */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = DB.CHART_ID)
    val chartId: Long?,

    /** Название диаграммы */
    @ColumnInfo(name = DB.COLUMN_NAME)
    val name: String,

    /** Количество цифр после запятой в подписи по оси Y */
    @ColumnInfo(name = DB.COUNT_DECIMAL)
    val countDecimal: Int,

    /** Тип подписи по оси X */
    @ColumnInfo(name = DB.X_VALUE_TYPE)
    val xValueType: DB.ValueTypes,

    /** Вариант отображения даты при xValueType = DB.ValueTypes.DATE */
    @ColumnInfo(name = DB.X_VALUE_DATE_FORMAT)
    val xValueDateFormat: DB.DateTypes?,

    /** Название оси X */
    @ColumnInfo(name = DB.X_NAME)
    val xName: String = "X",

    /** Название оси Y */
    @ColumnInfo(name = DB.Y_NAME)
    val yName: String = "Y"
)
