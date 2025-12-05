package dev.vknrk.app.input

import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow

interface InputProvider {

    val inputFlow: Flow<Input>

    suspend fun startReadingInput() : Job

    enum class Input {
        UP, DOWN, LEFT, RIGHT, QUIT, NEXT, PREVIOUS
    }
}
