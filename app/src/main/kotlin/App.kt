package dev.vknrk.app

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

data class Board(val width: Int, val height: Int) {


    private val cells: IntArray = generateField()
    private var emptyCellIndex = width * height - 1

    private fun generateField(): IntArray {
        val initArray = IntArray(width * height) { it + 1 }
        initArray.sliceArray(0 until (width * height) - 1).also {
            it.shuffle()
            it.forEachIndexed { index, i ->
                initArray[index] = it[index]
            }
        }
        return arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16).toIntArray()
//        return initArray
    }

    fun moveLeft() {
        val emptyX = emptyCellIndex % width
        val emptyY = emptyCellIndex / width
        if (emptyX > 0) {
            swapCells(emptyX, emptyY, emptyX - 1, emptyY)
            emptyCellIndex -= 1
        }
    }

    fun moveRight() {
        val emptyX = emptyCellIndex % width
        val emptyY = emptyCellIndex / width
        if (emptyX < width - 1) {
            swapCells(emptyX, emptyY, emptyX + 1, emptyY)
            emptyCellIndex += 1
        }
    }

    fun moveUp() {
        val emptyX = emptyCellIndex % width
        val emptyY = emptyCellIndex / width
        if (emptyY > 0) {
            swapCells(emptyX, emptyY, emptyX, emptyY -1)
            emptyCellIndex -= width
        }
    }

    fun moveDown() {
        val emptyX = emptyCellIndex % width
        val emptyY = emptyCellIndex / width
        if (emptyY < height - 1) {
            swapCells(emptyX, emptyY, emptyX, emptyY + 1)
            emptyCellIndex += width
        }
    }

    fun isSolved(): Boolean {
        for (i in 0 until width * height - 1) {
            if (cells[i] != i + 1) {
                return false
            }
        }
        return true
    }

    fun getCell(x: Int, y: Int): Int {
        if ( validateNew(x, y)) {
            throw IndexOutOfBoundsException("Cell ($x, $y) is out of bounds")
        }
        return cells[y * width + x]
    }

    fun swapCells(x1: Int, y1: Int, x2: Int, y2: Int) {
        val temp = getCell(x1, y1)
        setCell(x1, y1, getCell(x2, y2))
        setCell(x2, y2, temp)

    }

    private fun setCell(x: Int, y: Int, value: Int) {
        require(!validateNew(x, y)) { "Cell ($x, $y) is out of bounds" }
        cells[y * width + x] = value
    }

    private fun validateNew(x: Int, y: Int) : Boolean {
        return (x !in 0..<width || x !in 0..<height)
    }
}

data class Game(val board: Board, val inputFlow: SharedFlow<InputProvider.Input>) {
    private val _changed: MutableSharedFlow<Unit> = MutableSharedFlow(1)
    val changed: SharedFlow<Unit> = _changed.asSharedFlow()

    private val movesMap = mapOf(
        InputProvider.Input.UP to Pair(0, -1),
        InputProvider.Input.DOWN to Pair(0, 1),
        InputProvider.Input.LEFT to Pair(-1, 0),
        InputProvider.Input.RIGHT to Pair(1, 0)
    )

    suspend fun start() = coroutineScope {
        launch(Dispatchers.IO) {
            inputFlow.collect { input ->
                when (input) {
                    InputProvider.Input.UP -> {
                        print("Moving up\n")
                        board.moveUp();
                        if (!board.isSolved()) {
                            _changed.emit(Unit)
                        } else {
                            println("Congratulations! You've solved the puzzle!")
                        }
                    }

                    InputProvider.Input.DOWN -> {
                        board.moveDown();
                        if (!board.isSolved()) {
                            _changed.emit(Unit)
                        } else {
                            println("Congratulations! You've solved the puzzle!")
                        }
                    }

                    InputProvider.Input.LEFT -> {
                        board.moveLeft();
                        if (!board.isSolved()) {
                            _changed.emit(Unit)
                        } else {
                            println("Congratulations! You've solved the puzzle!")
                        }
                    }

                    InputProvider.Input.RIGHT -> {
                        board.moveRight();
                        if (!board.isSolved()) {
                            _changed.emit(Unit)
                        } else {
                            println("Congratulations! You've solved the puzzle!")
                        }
                    }

                    InputProvider.Input.QUIT -> {}
                    InputProvider.Input.NONE -> {}
                }
            }
        }
        while (true) {
            kotlinx.coroutines.delay(500)
        }
    }
}

class ConsoleGameRenderer() : Renderer {
    private val renderScope = CoroutineScope(Dispatchers.IO)

    override fun render(game: Game) {
        gameChanged(game)
        renderScope.launch {
            game.changed
                .collect { gameChanged(game) }
        }
    }

    fun clearConsole() {
        print("\u001b[2J") // Clear entire screen
        print("\u001b[0;0H") // Move cursor to top-left corner
        System.out.flush() // Ensure immediate output
    }

    private fun gameChanged(game: Game) {
        clearConsole()
        for (y in 0 until game.board.height) {
            for (x in 0 until game.board.width) {
                val cellValue = game.board.getCell(x, y)
                if (cellValue == game.board.width * game.board.height) {
                    print("|* ")
                    continue
                } else {
                    if (cellValue < 10) {
                        print("| ${game.board.getCell(x, y)}")
                    } else {
                        print("|${game.board.getCell(x, y)}")
                    }
                }
            }
            println("| ")
        }
    }
}

class ConsoleInputProvider() : InputProvider {
    private val _inputFlow = MutableSharedFlow<InputProvider.Input>(1)
    override val inputFlow: SharedFlow<InputProvider.Input> = _inputFlow.asSharedFlow()

    suspend fun startReadingInput() = coroutineScope {
        launch(Dispatchers.IO) {
            while (true) {
                val input = System.console().readLine();
                val inputEnum = when (input?.lowercase()) {
                    "w" -> InputProvider.Input.UP
                    "s" -> InputProvider.Input.DOWN
                    "a" -> InputProvider.Input.LEFT
                    "d" -> InputProvider.Input.RIGHT
                    "q" -> InputProvider.Input.QUIT
                    else -> InputProvider.Input.NONE
                }
                _inputFlow.emit(inputEnum)
            }
        }
    }
}

interface InputProvider {

    val inputFlow: SharedFlow<Input>

    enum class Input {
        UP, DOWN, LEFT, RIGHT, QUIT, NONE
    }
}

interface Renderer {
    fun render(game: Game)
}

fun main() = runBlocking(Dispatchers.Default) {
    val board = Board(4, 4)
    val inputProvider: InputProvider = ConsoleInputProvider()
    val game = Game(board, inputProvider.inputFlow)
    val renderer: Renderer = ConsoleGameRenderer()
    launch {
        (inputProvider as ConsoleInputProvider).startReadingInput()
    }
    renderer.render(game)
    game.start()
}
