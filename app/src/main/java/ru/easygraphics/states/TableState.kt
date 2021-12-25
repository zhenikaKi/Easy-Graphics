package ru.easygraphics.states

sealed class TableState<T>

data class Success<T>(val value: T) : TableState<T>()

data class Error<T>(val value: Throwable) : TableState<T>()