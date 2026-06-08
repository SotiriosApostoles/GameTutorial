package io.github.eknekron.mysticwoodstutorial.system

import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World
import com.github.quillraven.fleks.collection.compareEntityBy
import io.github.eknekron.mysticwoodstutorial.component.ImageComponent

class RenderSystem(
    private val stage: Stage = World.Companion.inject("GameStage")
) : IteratingSystem(
    World.family { all(ImageComponent.Companion) },
    compareEntityBy(ImageComponent.Companion)
) {

    override fun onTick() {
        super.onTick()

        with(stage) {
            viewport.apply()
            act(deltaTime)
            draw()
        }
    }

    override fun onTickEntity(entity: Entity) {
        entity[ImageComponent.Companion].image.toFront()
    }
}
