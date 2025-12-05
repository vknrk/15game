package dev.vknrk.app.render.console

class ConsolePrinterAdapter() : Printer {
    private val buffer = StringBuffer()
    override suspend fun clear() {
        print("\u001b[2J") // Clear entire screen
        print("\u001b[0;0H") // Move cursor to top-left corner
        System.out.flush() // Ensure immediate output
    }

    override suspend fun addMessage(message: String) {
        buffer.append(message)
    }

    override suspend fun flush() {
        print(buffer)
        buffer.setLength(0)
        System.out.flush()
    }
}
