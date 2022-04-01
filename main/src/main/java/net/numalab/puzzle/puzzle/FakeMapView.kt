package net.numalab.puzzle.puzzle

import org.bukkit.World
import org.bukkit.map.MapRenderer
import org.bukkit.map.MapView
import kotlin.random.Random

class FakeMapView : MapView {
    override fun getId(): Int = Random.nextInt(10000, Int.MAX_VALUE)
    override fun isVirtual(): Boolean = true
    override fun getScale(): MapView.Scale = MapView.Scale.FARTHEST
    override fun setScale(scale: MapView.Scale) {
    }

    override fun getCenterX(): Int = 0
    override fun getCenterZ(): Int = 0
    override fun setCenterX(x: Int) {
    }

    override fun setCenterZ(z: Int) {
    }

    override fun getWorld(): World? = null
    override fun setWorld(world: World) {
    }

    private val renderers = mutableListOf<MapRenderer>()
    override fun getRenderers(): MutableList<MapRenderer> {
        return renderers.toMutableList()
    }

    override fun addRenderer(renderer: MapRenderer) {
        renderers.add(renderer)
    }

    override fun removeRenderer(renderer: MapRenderer?): Boolean {
        return renderers.remove(renderer)
    }

    override fun isTrackingPosition(): Boolean = false
    override fun setTrackingPosition(trackingPosition: Boolean) {
    }

    override fun isUnlimitedTracking(): Boolean = false
    override fun setUnlimitedTracking(unlimited: Boolean) {
    }

    override fun isLocked(): Boolean = true
    override fun setLocked(locked: Boolean) {
    }
}