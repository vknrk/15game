package dev.vknrk.app.render

import dev.vknrk.app.Game
import kotlinx.coroutines.flow.Flow

interface Renderer {
    fun render(gameChanged: Flow<Game.GameChangeEvent>)
}
