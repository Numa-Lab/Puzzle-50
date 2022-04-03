package net.numalab.puzzle.map

import org.bukkit.entity.Player
import org.bukkit.map.MapCanvas
import org.bukkit.map.MapRenderer
import org.bukkit.map.MapView

class ColorSetMapRenderer(var arr: ByteArray, private val defaultColor: Byte?, var width: Int,val height:Int) : MapRenderer() {
    override fun render(map: MapView, canvas: MapCanvas, player: Player) {
        for (i in 0 until width) {
            for (j in 0 until height) {
                val index = j * width + i
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