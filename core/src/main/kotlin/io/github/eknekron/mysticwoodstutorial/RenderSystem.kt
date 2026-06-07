package io.github.eknekron.mysticwoodstutorial

import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World.Companion.family
import com.github.quillraven.fleks.World.Companion.inject
import com.github.quillraven.fleks.collection.compareEntityBy
import io.github.eknekron.mysticwoodstutorial.component.ImageComponent

class RenderSystem(
    private val stage: Stage = inject("GameStage")
) : IteratingSystem(
    family { all(ImageComponent) },
    compareEntityBy(ImageComponent)
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
        entity[ImageComponent].image.toFront()
    }
}
