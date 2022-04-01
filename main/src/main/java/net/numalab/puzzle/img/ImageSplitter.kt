package net.numalab.puzzle.img

import java.awt.image.BufferedImage
import kotlin.math.ceil

class ImageSplitter {
    /**
     * 画像を縦[height]横[width]のサイズに分割する
     */
    fun split(img: BufferedImage, width: Int, height: Int): MutableMap<Pair<Int, Int>, BufferedImage> {
        val result = mutableMapOf<Pair<Int,Int>,BufferedImage>()

        val xColumn = ceil(img.width / 128.0).toInt()
        val yRow = ceil(img.height / 128.0).toInt()

        for (x in 0 until xColumn) {
            for (y in 0 until yRow) {
                val fromX = x * width + 1
                val fromY = y * height + 1
                val w = if (x == xColumn - 1) img.width - fromX else width
                val h = if (y == yRow - 1) img.height - fromY else height

                result[x to y] = img.getSubimage(fromX, fromY, w, h)
            }
        }

        return result
    }
}