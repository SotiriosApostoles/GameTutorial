package io.github.eknekron.mysticwoodstutorial.system

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World.Companion.inject
import com.github.quillraven.fleks.World.Companion.family
import io.github.eknekron.mysticwoodstutorial.component.AnimationComponent
import io.github.eknekron.mysticwoodstutorial.component.AnimationComponent.Companion.NO_ANIMATION
import io.github.eknekron.mysticwoodstutorial.component.ImageComponent
import ktx.app.gdxError
import ktx.collections.map
import ktx.log.logger

class AnimationSystem(
    private val textureAtlas: TextureAtlas = inject("GameAtlas")
) : IteratingSystem(family { all(AnimationComponent.Companion, ImageComponent.Companion) }) {

    private val cachedAnimations = mutableMapOf<String, Animation<TextureRegionDrawable>>()

    override fun onTickEntity(entity: Entity) {
        val currentAnimation = entity[AnimationComponent]

        if (currentAnimation.nextAnimation == NO_ANIMATION) {
            currentAnimation.stateTime += deltaTime
        } else {
            currentAnimation.animation = animation(currentAnimation.nextAnimation)
            currentAnimation.stateTime = 0f
            currentAnimation.nextAnimation = NO_ANIMATION
        }

        currentAnimation.animation.playMode = currentAnimation.playMode
        entity[ImageComponent].image.drawable = currentAnimation.animation.getKeyFrame(currentAnimation.stateTime)
    }

    private fun animation(animationKeyPath: String): Animation<TextureRegionDrawable> {
        return cachedAnimations.getOrPut(animationKeyPath) {
            log.debug { "New animation is created for $animationKeyPath" }
            val regions = textureAtlas.findRegions(animationKeyPath)
            if (regions.isEmpty) {
                gdxError("There are no texture regions defined for $animationKeyPath")
            }
            Animation(
                DEFAULT_TIME_DURATION,
                regions.map { TextureRegionDrawable(it) }
            )
        }
    }

    companion object {
        private val log = logger<AnimationSystem>()
        private const val DEFAULT_TIME_DURATION = 1/8f
    }
}

