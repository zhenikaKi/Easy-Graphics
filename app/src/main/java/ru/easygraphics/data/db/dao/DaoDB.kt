package ru.easygraphics.data.db.dao

interface DaoDB {
    fun chartDao(): ChartDao
    fun chartLineDao(): ChartLineDao
    fun horizontalValueDao(): HorizontalValueDao
    fun verticalValueDao(): VerticalValueDao
}