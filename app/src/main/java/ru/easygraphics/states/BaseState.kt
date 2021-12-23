package ru.easygraphics.states

/**
 * Базовое состояние, содержащее ошибку.
 * Если в sealed class наследоваться от данного sealed interface, то во ViewModel<BaseState>
 * можно передавать состояния как из sealed class, так и из данного sealed interface.
 */
sealed interface BaseState {
    data class ErrorState(val text: String): BaseState
    object Loading: BaseState
}