package io.github.eknekron.mysticwoodstutorial.screen

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.github.quillraven.fleks.World
import com.github.quillraven.fleks.configureWorld
import io.github.eknekron.mysticwoodstutorial.system.RenderSystem
import io.github.eknekron.mysticwoodstutorial.component.AnimationComponent
import io.github.eknekron.mysticwoodstutorial.component.AnimationModel
import io.github.eknekron.mysticwoodstutorial.component.AnimationType
import io.github.eknekron.mysticwoodstutorial.component.ImageComponent
import io.github.eknekron.mysticwoodstutorial.system.AnimationSystem
import ktx.app.KtxScreen
import ktx.assets.disposeSafely
import ktx.log.logger

class GameScreen : KtxScreen {
    private val stage : Stage = Stage(ExtendViewport(16f, 9f))
    private val textureAtlas = TextureAtlas("assets/graphics/game.atlas")
    private val world : World = configureWorld {
        injectables {
            add("GameStage", stage)
            add("GameAtlas", textureAtlas)
        }

        systems {
            add(RenderSystem())
            add(AnimationSystem())
        }
    }

    override fun show() {
        log.debug { "GameScreen gets shown" }

        world.entity {
            it += ImageComponent().apply {
                image = Image().apply {
                    setSize(4f, 4f)
                }
            }
            it += AnimationComponent().apply {
                nextAnimation(AnimationModel.PLAYER, AnimationType.IDLE)
            }
        }

        world.entity {
            it += ImageComponent().apply {
                image = Image().apply {
                    setSize(4f, 4f)
                    setPosition(12f, 0f)
                }
            }
            it += AnimationComponent().apply {
                nextAnimation(AnimationModel.SLIME, AnimationType.RUN)
            }
        }
    }

    override fun render(delta: Float) {
        world.update(delta)
    }

    override fun dispose() {
        stage.disposeSafely()
        textureAtlas.disposeSafely()
        world.dispose()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    companion object {
        private val log = logger<GameScreen>()
    }
}
