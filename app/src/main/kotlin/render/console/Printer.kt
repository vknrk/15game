package dev.vknrk.app.render.console

interface Printer {
    suspend fun clear()
    suspend fun addMessage(message: String)
    suspend fun flush()
}
