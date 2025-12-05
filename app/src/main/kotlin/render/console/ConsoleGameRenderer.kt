package dev.vknrk.app.render.console

import dev.vknrk.app.Board
import dev.vknrk.app.Game
import dev.vknrk.app.render.Renderer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ConsoleGameRenderer(val printer: Printer = ConsolePrinterAdapter()) : Renderer {
    private val renderScope = CoroutineScope(Dispatchers.IO)
    private var title: String? = null
    private var board: Board? = null
    private var footer: String? = null

    override fun render(gameChanged: Flow<Game.GameChangeEvent>) {
        renderScope.launch {
            gameChanged
                .collect { event ->
                    when (event) {
                        Game.GameChangeEvent.Solved,Game.GameChangeEvent.Quit -> Unit

                        is Game.GameChangeEvent.BoardChanged -> {
                            board = event.board
                            drawScene()
                        }

                        is Game.GameChangeEvent.GameStarted -> {
                            title = event.game.getTitle()
                            footer = event.game.getFooter()
                            board = event.game.board
                            drawScene()
                        }

                        is Game.GameChangeEvent.TitlesChanged -> {
                            title = event.title
                            footer = event.footer
                            drawScene()
                        }
                    }
                }
        }
    }

    private suspend fun drawScene() {
        printer.clear()
        title?.let { drawTitle(it) }
        board?.let { drawGameBoard(it) }
        footer?.let { drawFooter(it) }
        printer.flush()
    }

    private suspend fun drawTitle(title: String) {
        printer.addMessage("$title\n\n")
    }

    private suspend fun drawFooter(footer: String) {
        printer.addMessage("\n$footer\n")
    }

    private suspend fun drawGameBoard(board: Board) {
        for (y in 0 until board.height) {
            for (x in 0 until board.width) {
                if (board.isEmptyCell(x, y)) {
                    printer.addMessage("| *")
                } else {
                    val cellValue = board.getCell(x, y)
                    if (cellValue < 10) {
                        printer.addMessage("| $cellValue")
                    } else {
                        printer.addMessage("|$cellValue")
                    }
                }
            }
            printer.addMessage("| \n")
        }
    }
}
