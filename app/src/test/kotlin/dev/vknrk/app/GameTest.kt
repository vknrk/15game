package dev.vknrk.app

import dev.vknrk.app.input.InputProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class GameTest {

    private class MockInputProvider(
        override val inputFlow: Flow<InputProvider.Input>
    ) : InputProvider {
        override suspend fun startReadingInput(): Job = Job()
    }

    private class SolvedBoardFactory : BoardFieldArrayFactory {
        override fun create(width: Int, height: Int): IntArray {
            return IntArray(width * height) { it + 1 }
        }
    }

    private class AlmostSolvedBoardFactory : BoardFieldArrayFactory {
        override fun create(width: Int, height: Int): IntArray {
            val array = IntArray(width * height) { it + 1 }
            // Swap last two elements so one RIGHT move solves it
            // [1, 2, ..., 14, 16, 15] with empty at index 14
            array[width * height - 2] = width * height
            array[width * height - 1] = width * height - 1
            return array
        }
    }

    @Test
    fun `should emit GameStarted when game is started`(): Unit = runBlocking {
        // GIVEN
        val board = Board(4, 4, SolvedBoardFactory())
        val inputProvider = MockInputProvider(flowOf())
        val game = Game(board, inputProvider)
        val events = Channel<Game.GameChangeEvent>(Channel.UNLIMITED)
        val scope = CoroutineScope(SupervisorJob())
        scope.launch { game.changed.collect { events.send(it) } }
        scope.launch { game.start() }

        // WHEN
        val event = events.receive()

        // THEN
        assertTrue(event is Game.GameChangeEvent.GameStarted)
        assertEquals(game, (event as Game.GameChangeEvent.GameStarted).game)
    }

    @ParameterizedTest
    @CsvSource(
        "UP",
        "DOWN",
        "LEFT",
        "RIGHT"
    )
    fun `should emit BoardChanged when move input is received`(
        inputName: String
    ): Unit = runBlocking {
        // GIVEN
        val board = Board(4, 4, SolvedBoardFactory())
        val inputProvider = MockInputProvider(flowOf(InputProvider.Input.valueOf(inputName)))
        val game = Game(board, inputProvider)
        val events = Channel<Game.GameChangeEvent>(Channel.UNLIMITED)
        val scope = CoroutineScope(SupervisorJob())
        scope.launch { game.changed.collect { events.send(it) } }
        scope.launch { game.start() }
        events.receive() // consume GameStarted

        // WHEN
        val event = events.receive()

        // THEN
        assertTrue(event is Game.GameChangeEvent.BoardChanged)
        assertEquals(board, (event as Game.GameChangeEvent.BoardChanged).board)
    }

    @Test
    fun `should emit Solved when board becomes solved after move`(): Unit = runBlocking {
        // GIVEN
        val board = Board(4, 4, AlmostSolvedBoardFactory())
        val inputProvider = MockInputProvider(flowOf(InputProvider.Input.RIGHT))
        val game = Game(board, inputProvider)
        val events = Channel<Game.GameChangeEvent>(Channel.UNLIMITED)
        val scope = CoroutineScope(SupervisorJob())
        scope.launch { game.changed.collect { events.send(it) } }
        scope.launch { game.start() }
        events.receive() // consume GameStarted

        // WHEN
        val boardChangedEvent = events.receive()

        // THEN
        assertTrue(boardChangedEvent is Game.GameChangeEvent.BoardChanged)

        // AND THEN
        val solvedEvent = events.receive()
        assertTrue(solvedEvent is Game.GameChangeEvent.Solved)
    }

    @Test
    fun `should emit Quit when quit input is received`(): Unit = runBlocking {
        // GIVEN
        val board = Board(4, 4, SolvedBoardFactory())
        val inputProvider = MockInputProvider(flowOf(InputProvider.Input.QUIT))
        val game = Game(board, inputProvider)
        val events = Channel<Game.GameChangeEvent>(Channel.UNLIMITED)
        val scope = CoroutineScope(SupervisorJob())
        scope.launch { game.changed.collect { events.send(it) } }
        scope.launch { game.start() }
        events.receive() // consume GameStarted

        // WHEN
        val event = events.receive()

        // THEN
        assertTrue(event is Game.GameChangeEvent.Quit)
    }
}
