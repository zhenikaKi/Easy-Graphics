package ru.easygraphics.states

sealed class ChartsListState:BaseState {
    data class Success(val chartsList:List<Pair<Long,String>>): ChartsListState()
}