package dev.vknrk.app.render.console

import dev.vknrk.app.Board
import dev.vknrk.app.BoardFieldArrayFactory
import dev.vknrk.app.Game
import dev.vknrk.app.input.InputProvider
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ConsoleGameRendererTest {

    private class MockPrinter : Printer {
        private val buffer = StringBuilder()
        private val _output = Channel<String>(Channel.UNLIMITED)
        val output: Channel<String> = _output

        override suspend fun clear() {
            buffer.clear()
        }

        override suspend fun addMessage(message: String) {
            buffer.append(message)
        }

        override suspend fun flush() {
            _output.send(buffer.toString())
            buffer.clear()
        }
    }

    private class MockInputProvider : InputProvider {
        override val inputFlow = MutableSharedFlow<InputProvider.Input>()
    }

    @Test
    fun `should render board when GameStarted event received`(): Unit = runBlocking {
        // GIVEN
        val mockPrinter = MockPrinter()
        val renderer = ConsoleGameRenderer(mockPrinter)
        val events = Channel<Game.GameChangeEvent>()
        val board = Board(4, 4) { _, _ -> intArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16) }
        val game = Game(board, MockInputProvider())
        renderer.render(events.consumeAsFlow())
        val expectedString = listOf(
            game.getTitle()+"\n\n",
            "| 1| 2| 3| 4| \n",
            "| 5| 6| 7| 8| \n",
            "| 9|10|11|12| \n",
            "|13|14|15| *| \n",
            "\n",
            game.getFooter(),
            "\n"
        ).joinToString("")

        // WHEN
        events.send(Game.GameChangeEvent.GameStarted(game))

        // THEN
        val actualRows = mockPrinter.output.receive()
        assertEquals(expectedString, actualRows)
    }

    @Test
    fun `should render board when BoardChanged event received`(): Unit = runBlocking {
        // GIVEN
        val mockPrinter = MockPrinter()
        val renderer = ConsoleGameRenderer(mockPrinter)
        val events = Channel<Game.GameChangeEvent>()
        val board = Board(4, 4, BoardFieldArrayFactory { _, _ -> intArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16) })
        renderer.render(events.consumeAsFlow())
        val expectedString = listOf(
            "| 1| 2| 3| 4| \n",
            "| 5| 6| 7| 8| \n",
            "| 9|10|11|12| \n",
            "|13|14|15| *| \n"
        ).joinToString("")

        // WHEN
        events.send(Game.GameChangeEvent.BoardChanged(board))

        // THEN
        val actualRows = mockPrinter.output.receive()
        assertEquals(expectedString, actualRows)
    }

    @Test
    fun `should do nothing when Solved event received`(): Unit = runBlocking {
        // GIVEN
        val mockPrinter = MockPrinter()
        val renderer = ConsoleGameRenderer(mockPrinter)
        val events = Channel<Game.GameChangeEvent>()
        renderer.render(events.consumeAsFlow())

        // WHEN
        events.send(Game.GameChangeEvent.Solved)

        // THEN
        assertTrue(mockPrinter.output.isEmpty)
    }

    @Test
    fun `should do nothing when Quit event received`(): Unit = runBlocking {
        // GIVEN
        val mockPrinter = MockPrinter()
        val renderer = ConsoleGameRenderer(mockPrinter)
        val events = Channel<Game.GameChangeEvent>()
        renderer.render(events.consumeAsFlow())

        // WHEN
        events.send(Game.GameChangeEvent.Quit)

        // THEN
        assertTrue(mockPrinter.output.isEmpty)
    }
}
