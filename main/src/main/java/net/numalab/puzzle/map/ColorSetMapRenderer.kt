package net.numalab.puzzle.map

import org.bukkit.entity.Player
import org.bukkit.map.MapCanvas
import org.bukkit.map.MapRenderer
import org.bukkit.map.MapView

class ColorSetMapRenderer(var arr: ByteArray, private val defaultColor: Byte?) : MapRenderer() {
    override fun render(map: MapView, canvas: MapCanvas, player: Player) {
        for (i in 0 until 128) {
            for (j in 0 until 128) {
                val index = j * 128 + i
                try {
                    canvas.setPixel(i, j, arr[index])
                } catch (e: IndexOutOfBoundsException) {
                    if (defaultColor != null) {
                        canvas.setPixel(i, j, defaultColor)
                    }
                }
            }
        }
    }
}