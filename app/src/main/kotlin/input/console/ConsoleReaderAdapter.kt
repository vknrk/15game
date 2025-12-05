package dev.vknrk.app.input.console

class ConsoleReaderAdapter : Reader {
    override suspend fun readLine(): String? {
        return System.console()?.readLine()
    }
}
