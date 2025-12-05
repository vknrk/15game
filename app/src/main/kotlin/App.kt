package dev.vknrk.app

import dev.vknrk.app.input.console.ConsoleInputProvider
import dev.vknrk.app.input.console.InputProviderWithHistory
import dev.vknrk.app.render.console.ConsoleGameRenderer
import dev.vknrk.app.render.Renderer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() = runBlocking(Dispatchers.Default) {
    val board = Board(4, 4)
    val inputProvider = InputProviderWithHistory(ConsoleInputProvider())
    val game = Game(board, inputProvider)
    val renderer: Renderer = ConsoleGameRenderer()
    val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    scope.launch { inputProvider.startReadingInput() }
    scope.launch { renderer.render(game.changed) }
    scope.launch { game.start() }.join()
}
