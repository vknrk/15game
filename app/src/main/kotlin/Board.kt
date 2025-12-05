package dev.vknrk.app

data class Board(
    val width: Int,
    val height: Int,
    val fieldInitializer: BoardFieldArrayFactory? = null
) {

    enum class MoveDirection(val dx: Int, val dy: Int) {
        LEFT(-1, 0), RIGHT(1, 0), UP(0, -1), DOWN(0, 1);
    }


    private val cells: IntArray = (fieldInitializer ?: RubikLikeBoardFieldArrayFactory()).create(width, height).also {
        require(it.size == width * height) { "Field initializer returned array of invalid size." }
    }

    private var emptyCellIndex = cells.indexOf(cells.size)

    fun move(direction: MoveDirection) {
        val (dx, dy) = (direction.dx * 1) to (direction.dy * width)
        val newIndex = emptyCellIndex + dy + dx
        if (newIndex in 0 until cells.size) {
            val temp = cells[newIndex]
            cells[newIndex] = cells.size
            cells[emptyCellIndex] = temp
            emptyCellIndex = newIndex
        }
    }

    fun isSolved(): Boolean {
        for (i in 0 until cells.size) {
            if (cells[i] != i + 1) {
                return false
            }
        }
        return true
    }


    fun getCell(x: Int, y: Int): Int {
        require(isValidCellCoordinates(x, y)) { "Invalid cell coordinates." }
        return cells[y * width + x]
    }

    fun isEmptyCell(x: Int, y: Int): Boolean {
        require(isValidCellCoordinates(x, y)) { "Invalid cell coordinates." }
        return x + y * width == emptyCellIndex
    }

    private fun isValidCellCoordinates(x: Int, y: Int): Boolean {
        return (x in 0..<width && y in 0..<height)
    }
}
