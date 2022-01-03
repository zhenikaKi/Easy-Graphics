package ru.easygraphics.data.domain

data class TableLineData(
    val DbId: Long? = 0,
    val LineId: Int = 0,
    val LineName: String = "",
    val LineValue: MutableList<LineDetails> = mutableListOf()
){

    fun addLineValue(lineValue: String, columnWidth: Int, isHead: Boolean) {
        this.LineValue.add(LineDetails(lineValue, columnWidth, isHead))
    }
}
