package net.numalab.puzzle.puzzle

import org.bukkit.entity.Player
import org.bukkit.map.MapCanvas
import org.bukkit.map.MapPalette
import org.bukkit.map.MapRenderer
import org.bukkit.map.MapView
import java.awt.image.BufferedImage

class ImageMapRenderer(private val img: BufferedImage) : MapRenderer() {
    override fun render(map: MapView, canvas: MapCanvas, player: Player) {
        // reset all cursors
        repeat(canvas.cursors.size()) {
            val c = canvas.cursors.getCursor(0)
            canvas.cursors.removeCursor(c)
        }

        // draw image
        canvas.drawImage(0,0,img)
    }
}