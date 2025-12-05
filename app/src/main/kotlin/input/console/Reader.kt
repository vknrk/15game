package dev.vknrk.app.input.console

interface Reader {
    suspend fun readLine(): String?
}
