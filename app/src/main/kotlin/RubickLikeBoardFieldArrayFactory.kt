package dev.vknrk.app

import dev.vknrk.utils.swap

data class RubickLikeBoardFieldArrayFactory(
    val randomStepsCount: Int = 200
) : BoardFieldArrayFactory {
    override fun create(width: Int, height: Int): IntArray {
        var emptyCellIndex = width * height - 1
        val initArray = IntArray(width * height) { it + 1 }
        val randomSteps: ArrayList<Board.MoveDirection> = ArrayList(randomStepsCount)
        val firstMove = initArray.findPossibleMoves(emptyCellIndex, width).random()
        emptyCellIndex = performMove(emptyCellIndex, width, initArray, firstMove)
        randomSteps.add(firstMove)
        for (i in 1 until randomStepsCount) {
            val previousMove = randomSteps[i - 1]
            val move = initArray.findPossibleMoves(emptyCellIndex, width)
                .filter { it != previousMove }
                .random()
            randomSteps.add(move)
            emptyCellIndex = performMove(emptyCellIndex, width, initArray, move)
        }
        while (emptyCellIndex + width < initArray.size-1) {
            emptyCellIndex = performMove(emptyCellIndex, width, initArray, Board.MoveDirection.DOWN)
        }
        while (emptyCellIndex < initArray.size-1) {
            emptyCellIndex = performMove(emptyCellIndex, width, initArray, Board.MoveDirection.RIGHT)
        }
        return initArray
    }

    private fun IntArray.findPossibleMoves(emptyCellIndex: Int, width: Int): List<Board.MoveDirection> {
        return Board.MoveDirection.entries.filter {
            val (dx, dy) = (it.dx * 1) to (it.dy * width)
            val newIndex = emptyCellIndex + dy + dx
            newIndex in 0 until size
        }
    }

    private fun performMove(
        emptyCellIndex: Int,
        width: Int,
        initArray: IntArray,
        move: Board.MoveDirection
    ): Int {
        val (dx, dy) = (move.dx * 1) to (move.dy * width)
        val newIndex = emptyCellIndex + dy + dx
        initArray.swap(emptyCellIndex, newIndex)
        return newIndex
    }
}
