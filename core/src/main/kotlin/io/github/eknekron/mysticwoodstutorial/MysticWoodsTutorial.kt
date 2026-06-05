package io.github.eknekron.mysticwoodstutorial

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import io.github.eknekron.mysticwoodstutorial.screen.GameScreen
import ktx.app.KtxGame
import ktx.app.KtxScreen

/** [com.badlogic.gdx.ApplicationListener] implementation shared by all platforms. */
class MysticWoodsTutorial : KtxGame<KtxScreen>() {

    override fun create() {
        Gdx.app.logLevel = Application.LOG_DEBUG
        addScreen(GameScreen())
        setScreen<GameScreen>()
    }
}
