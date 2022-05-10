package net.numalab.puzzle.map

import org.bukkit.map.MapPalette
import java.awt.Color
import java.awt.image.BufferedImage

class AdvancedMapPalette {
    companion object {
        private val cache = mutableMapOf<Color, Byte>()

        fun imageToBytes(img: BufferedImage): ByteArray {
            val pixels = IntArray(img.width * img.height)
            img.getRGB(0, 0, img.width, img.height, pixels, 0, img.width)

            val result = ByteArray(img.width * img.height)
            for (i in pixels.indices) {
                result[i] = getFromColor(Color(pixels[i], true))
            }
            return result
        }

        fun getFromColor(color: Color): Byte {
            val e = cache[color]
            return if (e != null) {
                e
            } else {
                val i = MapPalette.matchColor(color)
                cache[color] = i
                i
            }
        }
    }
}