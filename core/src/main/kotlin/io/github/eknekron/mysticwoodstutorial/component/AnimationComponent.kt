package io.github.eknekron.mysticwoodstutorial.component

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType

enum class AnimationModel {
    PLAYER, SLIME, CHEST, UNDEFINED;

    val atlasKey: String = this.toString().lowercase()
}

enum class AnimationType {
    IDLE, RUN, ATTACK, DEATH, OPEN;

    val atlasKey: String = this.toString().lowercase()
}

data class AnimationComponent (
    var model: AnimationModel = AnimationModel.UNDEFINED,
    var stateTime: Float = 0f,
    var playMode: Animation.PlayMode = Animation.PlayMode.LOOP
) : Component<AnimationComponent>
{
    lateinit var animation: Animation<TextureRegionDrawable>
    var nextAnimation: String = ""

    override fun type() : ComponentType<AnimationComponent> = AnimationComponent

    fun nextAnimation(model: AnimationModel, type: AnimationType) {
        this.model = model
        nextAnimation = "${model.atlasKey}/${type.atlasKey}"
    }

    companion object : ComponentType<AnimationComponent>() {
        val NO_ANIMATION = ""
    }
}
