package dev.vknrk.app.input

import dev.vknrk.app.input.console.ConsoleInputProvider
import dev.vknrk.app.input.console.Reader
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class ConsoleInputProviderTest {

    private class MockReader(private val responses: Channel<String?>) : Reader {
        override suspend fun readLine(): String? {
            return responses.receive()
        }
    }

    @ParameterizedTest
    @CsvSource(
        "w, UP",
        "s, DOWN",
        "a, LEFT",
        "d, RIGHT",
        "q, QUIT"
    )
    fun `should emit correct input when character is read`(input: String, expectedInput: String): Unit = runBlocking {
        // GIVEN
        val responses = Channel<String?>()
        val reader = MockReader(responses)
        val provider = ConsoleInputProvider(reader)
        val expected = InputProvider.Input.valueOf(expectedInput)

        // WHEN
        val job = launch { provider.startReadingInput() }
        responses.send(input)

        // THEN
        assertEquals(expected, provider.inputFlow.first())
        job.cancelAndJoin()
    }

    @ParameterizedTest
    @CsvSource(
        "W, UP",
        "S, DOWN",
        "A, LEFT",
        "D, RIGHT",
        "Q, QUIT"
    )
    fun `should handle uppercase input`(input: String, expectedInput: String): Unit = runBlocking {
        // GIVEN
        val responses = Channel<String?>()
        val reader = MockReader(responses)
        val provider = ConsoleInputProvider(reader)
        val expected = InputProvider.Input.valueOf(expectedInput)

        // WHEN
        val job = launch { provider.startReadingInput() }
        responses.send(input)

        // THEN
        assertEquals(expected, provider.inputFlow.first())
        job.cancelAndJoin()
    }

    @Test
    fun `should ignore invalid input`(): Unit = runBlocking {
        // GIVEN
        val responses = Channel<String?>()
        val reader = MockReader(responses)
        val provider = ConsoleInputProvider(reader)
        val expected = InputProvider.Input.UP

        // WHEN
        val job = launch { provider.startReadingInput() }
        responses.send("x") // invalid
        responses.send("w") // valid

        // THEN
        assertEquals(expected, provider.inputFlow.first())
        job.cancelAndJoin()
    }
}
