package ru.easygraphics.data.domain

data class TableLineData(
    val dbId: Long? = 0,
    val lineId: Int = 0,
    val lineName: String = "",
    val lineValue: MutableList<LineDetails> = mutableListOf()
){

    fun addLineValue(lineValue: String, columnWidth: Int, isHead: Boolean) {
        this.lineValue.add(LineDetails(lineValue, columnWidth, isHead))
    }
}
