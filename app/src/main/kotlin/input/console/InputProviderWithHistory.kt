package dev.vknrk.app.input.console

import dev.vknrk.app.input.InputProvider
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.LinkedList

data class InputProviderWithHistory(
    val inputProvider: InputProvider,
    val maxHistorySize: Int = 50
) : InputProvider {

    private var currentPosition = 0

    private val history: LinkedList<InputProvider.Input> = LinkedList()
    private val _outputFlow = MutableSharedFlow<InputProvider.Input>()
    override val inputFlow: Flow<InputProvider.Input> = _outputFlow.asSharedFlow()

    override suspend fun startReadingInput(): Job = coroutineScope {
        launch {
            inputProvider.inputFlow.collect { input ->
                _outputFlow.emit(input)
                when (input) {
                    InputProvider.Input.PREVIOUS -> handleBack()
                    InputProvider.Input.NEXT -> handleForward()
                    else -> addToHistory(input)
                }
            }
        }
        launch { inputProvider.startReadingInput() }
    }

    private suspend fun handleForward() {
        if (currentPosition == 0) return
        val nextInput = history[currentPosition - 1]
        _outputFlow.emit(nextInput)
        currentPosition--
    }

    private suspend fun handleBack() {
        if (history.isEmpty()) return
        if (currentPosition >= maxHistorySize) return
        val previousInput = history[currentPosition]
        _outputFlow.emit(compensationAction(previousInput))
        currentPosition++
    }

    private fun addToHistory(input: InputProvider.Input) {
        while (history.size >= maxHistorySize) history.removeLast()
        history.add(input)
        currentPosition = 0
    }

    private fun compensationAction(input: InputProvider.Input): InputProvider.Input = when (input) {
        InputProvider.Input.UP -> InputProvider.Input.DOWN
        InputProvider.Input.DOWN -> InputProvider.Input.UP
        InputProvider.Input.LEFT -> InputProvider.Input.RIGHT
        InputProvider.Input.RIGHT -> InputProvider.Input.LEFT
        else -> input
    }
}
