package ru.easygraphics.states


sealed class DescriptionState:BaseState {
    data class Success(val chart_id:Long): DescriptionState()
}