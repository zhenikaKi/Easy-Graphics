package ru.easygraphics.data.db.entities

/** связка одного значения по оси X со значениями всех линий по оси Y */
data class HorizontalValueWithLinesValue(
    /** Значение по оси X */
    var horizontalValue: HorizontalValue,

    /**
     * Соответствующие значения по оси Y для всех линий графика. Каждый элемент списка - это значение
     * одной линии. Т.е если в графике 5 линий, то размер данного списка равен 5.
     * Т.е. на первом месте будут данные по Y, соответствующие horizontalValue первой линии,
     * на втором - соответствующие horizontalValue второй линии и т.д.
     */
    var verticalValues: List<VerticalValue?> = listOf()
)
