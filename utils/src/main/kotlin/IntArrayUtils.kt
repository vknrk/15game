package dev.vknrk.utils

fun IntArray.swap(i: Int, j: Int) {
    require(i in 0 until size && j in 0 until size) {
        "Invalid indices."
    }
    val tmp = this[i]
    this[i] = this[j]
    this[j] = tmp
}
