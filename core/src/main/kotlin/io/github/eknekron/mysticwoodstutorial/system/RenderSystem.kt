package io.github.eknekron.mysticwoodstutorial.system

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World
import com.github.quillraven.fleks.collection.compareEntityBy
import io.github.eknekron.mysticwoodstutorial.component.ImageComponent
import io.github.eknekron.mysticwoodstutorial.event.MapChangeEvent
import ktx.assets.disposeSafely
import ktx.graphics.use
import ktx.tiled.forEachLayer

class RenderSystem(
    private val stage: Stage = World.Companion.inject("GameStage")
) : EventListener, IteratingSystem(
    World.family { all(ImageComponent.Companion) },
    compareEntityBy(ImageComponent.Companion)
) {
    private val bgdLayers = mutableListOf<TiledMapTileLayer>()
    private val fgdLayers = mutableListOf<TiledMapTileLayer>()
    private val mapRenderer = OrthogonalTiledMapRenderer(null, 1/16f, stage.batch)
    private val orthoCam = stage.camera as OrthographicCamera

    override fun onTick() {
        super.onTick()

        with(stage) {
            viewport.apply()

            AnimatedTiledMapTile.updateAnimationBaseTime()
            mapRenderer.setView(orthoCam)

            if (bgdLayers.isNotEmpty()) {
                stage.batch.use(orthoCam.combined) {
                    bgdLayers.forEach { mapRenderer.renderTileLayer(it) }
                }
            }

            act(deltaTime)
            draw()

            if (fgdLayers.isNotEmpty()) {
                stage.batch.use(orthoCam.combined) {
                    fgdLayers.forEach { mapRenderer.renderTileLayer(it) }
                }
            }
        }
    }

    override fun onTickEntity(entity: Entity) {
        entity[ImageComponent.Companion].image.toFront()
    }

    override fun handle(event: Event?): Boolean {
        when {
            event is MapChangeEvent -> {
                bgdLayers.clear()
                fgdLayers.clear()

                event.map.forEachLayer<TiledMapTileLayer> { layer ->
                    if (layer.name.startsWith("fgd")) {
                        fgdLayers.add(layer)
                    } else {
                        bgdLayers.add(layer)
                    }
                }
                return true
            }
        }

        return false
    }

    override fun onDisable() {
        mapRenderer.disposeSafely()
    }
}
