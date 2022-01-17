package ru.easygraphics.data.dto

/** Вспомогательная сущность готового файла экспорта или импорта данных */
data class FileDto(
    val version: Int,
    val date: String,
    val charts: List<ChartDto>
)
