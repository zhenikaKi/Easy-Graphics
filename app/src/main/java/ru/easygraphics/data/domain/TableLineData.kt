package ru.easygraphics.data.domain

data class TableLineData(
    val DbId: Long? = 0,
    val LineId: Int = 0,
    val LineName: String = "",
    val LineValue: MutableList<Pair<String, Int>> = mutableListOf()
){

    fun addLineValue(lineValue: String, columnMaxWidth: Int) {
        this.LineValue.add(Pair(lineValue, columnMaxWidth))
    }
}
