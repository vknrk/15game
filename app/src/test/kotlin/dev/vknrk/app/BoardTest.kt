package dev.vknrk.app

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class BoardTest {

    private class SolvedBoardFactory : BoardFieldArrayFactory {
        override fun create(width: Int, height: Int): IntArray {
            return IntArray(width * height) { it + 1 }
        }
    }

    @Test
    fun `should initialize board in solved state`() {
        // GIVEN
        val board = Board(4, 4, SolvedBoardFactory())

        // WHEN
        val result = board.isSolved()

        // THEN
        assertTrue(result)
    }

    @ParameterizedTest
    @CsvSource(
        "0, 0, 1",
        "1, 0, 2",
        "3, 3, 16"
    )
    fun `should return correct cell value at position`(x: Int, y: Int, expected: Int) {
        // GIVEN
        val board = Board(4, 4, SolvedBoardFactory())

        // WHEN
        val actual = board.getCell(x, y)

        // THEN
        assertEquals(expected, actual)
    }

    @Test
    fun `should identify empty cell at last position`() {
        // GIVEN
        val board = Board(4, 4, SolvedBoardFactory())

        // WHEN
        val result = board.isEmptyCell(3, 3)

        // THEN
        assertTrue(result)
    }

    @Test
    fun `should move empty cell left`() {
        // GIVEN
        val board = Board(4, 4, SolvedBoardFactory())

        // WHEN
        board.move(Board.MoveDirection.LEFT)

        // THEN
        assertTrue(board.isEmptyCell(2, 3))
        assertEquals(15, board.getCell(3, 3))
    }

    @Test
    fun `should move empty cell up`() {
        // GIVEN
        val board = Board(4, 4, SolvedBoardFactory())

        // WHEN
        board.move(Board.MoveDirection.UP)

        // THEN
        assertTrue(board.isEmptyCell(3, 2))
        assertEquals(12, board.getCell(3, 3))
    }

    @Test
    fun `should not be solved after move`() {
        // GIVEN
        val board = Board(4, 4, SolvedBoardFactory())

        // WHEN
        board.move(Board.MoveDirection.LEFT)

        // THEN
        assertFalse(board.isSolved())
    }

    @Test
    fun `should throw when accessing invalid coordinates`() {
        // GIVEN
        val board = Board(4, 4, SolvedBoardFactory())

        // WHEN & THEN
        assertThrows(IllegalArgumentException::class.java) {
            board.getCell(4, 0)
        }
    }

    @Test
    fun `should throw when checking empty cell with invalid coordinates`() {
        // GIVEN
        val board = Board(4, 4, SolvedBoardFactory())

        // WHEN & THEN
        assertThrows(IllegalArgumentException::class.java) {
            board.isEmptyCell(0, 4)
        }
    }

    @Test
    fun `should throw when field initializer returns wrong size`() {
        // GIVEN
        val wrongSizeFactory = BoardFieldArrayFactory { _, _ -> intArrayOf(1, 2, 3) }

        // WHEN & THEN
        assertThrows(IllegalArgumentException::class.java) {
            Board(4, 4, wrongSizeFactory)
        }
    }

    @Test
    fun `should use default generator when initializer not provided`() {
        // GIVEN
        val board = Board(4, 4)

        // WHEN
        val cells = (0 until 16).map { i -> board.getCell(i % 4, i / 4) }

        // THEN
        assertEquals(16, cells.size)
    }
}
