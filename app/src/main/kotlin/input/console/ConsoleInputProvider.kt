package dev.vknrk.app.input.console

import dev.vknrk.app.input.InputProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class ConsoleInputProvider(private val reader: Reader = ConsoleReaderAdapter()) : InputProvider {
    private val _inputFlow = MutableSharedFlow<InputProvider.Input>(1)
    override val inputFlow: SharedFlow<InputProvider.Input> = _inputFlow.asSharedFlow()

    suspend fun startReadingInput() = coroutineScope {
        launch(Dispatchers.IO) {
            while (true) {
                val input = reader.readLine()
                val inputEnum = when (input?.lowercase()) {
                    "w" -> InputProvider.Input.UP
                    "s" -> InputProvider.Input.DOWN
                    "a" -> InputProvider.Input.LEFT
                    "d" -> InputProvider.Input.RIGHT
                    "q" -> InputProvider.Input.QUIT
                    else -> null
                }
                inputEnum?.let { _inputFlow.emit(it) }
            }
        }
    }
}
