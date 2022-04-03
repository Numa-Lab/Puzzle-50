package net.numalab.puzzle.map

import org.bukkit.entity.Player
import org.bukkit.map.MapCanvas
import org.bukkit.map.MapRenderer
import org.bukkit.map.MapView
import java.awt.Color
import java.awt.image.BufferedImage

class ImageMapRenderer(i: BufferedImage) : MapRenderer() {
    var img: BufferedImage = i
        set(value) {
            field = value
            converted = AdvancedMapPalette.imageToBytes(value)
            colorSetRenderer.arr = converted
        }
    private var converted = AdvancedMapPalette.imageToBytes(img)
    private val colorSetRenderer = ColorSetMapRenderer(converted, AdvancedMapPalette.getFromColor(Color.BLACK))
    override fun render(map: MapView, canvas: MapCanvas, player: Player) {
        // reset all cursors
        repeat(canvas.cursors.size()) {
            val c = canvas.cursors.getCursor(0)
            canvas.cursors.removeCursor(c)
        }

        // draw image
        colorSetRenderer.render(map, canvas, player)
    }
}