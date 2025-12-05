package dev.vknrk.app

fun interface BoardFieldArrayFactory {
    fun create(width: Int, height: Int): IntArray
}
