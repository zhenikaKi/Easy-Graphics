package ru.easygraphics.helpers.consts

//константы по базе данных
object DB {
    //база данных
    const val VERSION = 1
    const val NAME = "app.db"

    //таблицы
    const val TABLE_CHARTS = "charts"
    const val TABLE_CHART_LINES = "chart_lines"
    const val TABLE_HORIZONTAL_VALUE = "horizontal_values"
    const val TABLE_VERTICAL_VALUE = "vertical_values"

    //столбцы
    const val CHART_ID = "chart_id"
    const val COLUMN_NAME = "name"
    const val COUNT_DECIMAL = "count_decimal"
    const val X_VALUE_TYPE = "x_value_type"
    const val X_VALUE_DATE_FORMAT = "x_value_date_format"
    const val X_NAME = "x_name"
    const val Y_NAME = "y_name"
    const val LINE_ID = "line_id"
    const val COLOR = "color"
    const val X_VALUE_ID = "x_value_id"
    const val Y_VALUE_ID = "y_value_id"
    const val VALUE = "value"

    //типы подписи по оси X
    enum class ValueTypes(val value: Int, val title: String) {
        STRING(1, "Строка"),
        NUMBER(2, "Число"),
        DATE(3, "Дата");

        companion object {
            //получить значение из заголовка
            fun titleToValueTypes(title: String) =
                values().firstOrNull { item -> item.title == title }

            //получить список заголовков
            fun titles() = values().map { valueTypes -> valueTypes.title }
        }
    }

    //вариант отображения даты для ValueTypes.DATE
    enum class DateTypes(val value: Int, val title: String) {
        DD_MM_YYYY(1, "дд.мм.гггг"),
        DD_MM(2, "дд.мм"),
        YYYY_MM_DD(3, "гггг.мм.дд");

        companion object {
            //получить значение из заголовка
            fun titleToDateTypes(title: String) =
                values().firstOrNull { item -> item.title == title }

            //получить список заголовков
            fun titles() = values().map { valueTypes -> valueTypes.title }
        }
    }
}