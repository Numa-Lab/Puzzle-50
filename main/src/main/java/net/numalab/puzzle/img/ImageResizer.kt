package net.numalab.puzzle.img

import java.awt.image.BufferedImage

class ImageResizer {
    fun resize(img: BufferedImage, width: Int, height: Int): BufferedImage {
        val buffered = BufferedImage(width, height, img.type)
        val g = buffered.createGraphics()
        g.drawImage(img, 0, 0, width, height, null)
        g.dispose()
        return buffered
    }
}

