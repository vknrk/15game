package dev.vknrk.app

import dev.vknrk.app.input.InputProvider
import dev.vknrk.app.input.InputProvider.Input
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

data class Game(val board: Board, val inputProvider: InputProvider) {
    private var title = "Fifteen Puzzle"
    private val started = false
    private var footer = "Use W/A/S/D to move tiles, Q to quit. R/N to navigate history."

    sealed interface GameChangeEvent {
        data class GameStarted(val game: Game) : GameChangeEvent
        data class BoardChanged(val board: Board) : GameChangeEvent
        data class TitlesChanged(val title: String, val footer: String) : GameChangeEvent
        data object Solved : GameChangeEvent
        data object Quit : GameChangeEvent
    }

    private val _changed: MutableSharedFlow<GameChangeEvent> = MutableSharedFlow(1)

    private val inputHandlingStrategies = Input.entries.associateWith { entry ->
        suspend {
            when (entry) {
                Input.UP -> makeBoardMove(Board.MoveDirection.UP)
                Input.DOWN -> makeBoardMove(Board.MoveDirection.DOWN)
                Input.LEFT -> makeBoardMove(Board.MoveDirection.LEFT)
                Input.RIGHT -> makeBoardMove(Board.MoveDirection.RIGHT)
                Input.QUIT -> quitGame()
                else -> Unit
            }
        }
    }

    val changed: SharedFlow<GameChangeEvent> = _changed.asSharedFlow()

    fun getTitle() = title

    fun getFooter() = footer

    suspend fun start(): Job {
        if (started) throw IllegalStateException("Game already started")
        return coroutineScope {
            _changed.emit(GameChangeEvent.GameStarted(this@Game))
            launch { runGameLoop() }
        }
    }

    private suspend fun runGameLoop() = coroutineScope {
        inputProvider.inputFlow.collect { input ->
            inputHandlingStrategies[input]?.invoke()
        }
    }

    private suspend fun makeBoardMove(direction: Board.MoveDirection) {
        board.move(direction)
        if (board.isSolved()) {
            footer = "You solved the puzzle!"
            _changed.emit(GameChangeEvent.TitlesChanged(title, footer))
            _changed.emit(GameChangeEvent.BoardChanged(board))
            _changed.emit(GameChangeEvent.Solved)
            currentCoroutineContext().cancel()
        } else {
            _changed.emit(GameChangeEvent.BoardChanged(board))
        }
    }

    private suspend fun quitGame() {
        footer = "Game over."
        _changed.emit(GameChangeEvent.Quit)
        currentCoroutineContext().cancel()
    }
}
