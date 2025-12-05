package dev.vknrk.app.input

import kotlinx.coroutines.flow.Flow

interface InputProvider {

    val inputFlow: Flow<Input>

    enum class Input {
        UP, DOWN, LEFT, RIGHT, QUIT
    }
}
