package dev.vknrk.app.input.console

import dev.vknrk.app.input.InputProvider
import dev.vknrk.app.input.InputProvider.Input
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class ConsoleInputProvider(private val reader: Reader = ConsoleReaderAdapter()) : InputProvider {
    private val _inputFlow = MutableSharedFlow<Input>(1)
    override val inputFlow: SharedFlow<Input> = _inputFlow.asSharedFlow()

    override suspend fun startReadingInput() = coroutineScope {
        launch(Dispatchers.IO) {
            while (true) {
                val input = reader.readLine()
                val inputEnum = when (input?.lowercase()) {
                    "w" -> Input.UP
                    "s" -> Input.DOWN
                    "a" -> Input.LEFT
                    "d" -> Input.RIGHT
                    "q" -> Input.QUIT
                    "r" -> Input.PREVIOUS
                    "n" -> Input.NEXT
                    else -> null
                }
                inputEnum?.let { _inputFlow.emit(it) }
            }
        }
    }
}
