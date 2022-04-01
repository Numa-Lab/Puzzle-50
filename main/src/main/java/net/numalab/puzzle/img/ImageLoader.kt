package net.numalab.puzzle.img

import java.awt.image.BufferedImage
import java.io.IOException
import java.net.URL
import javax.imageio.ImageIO

class ImageLoader {
    fun loadImage(url: URL): BufferedImage? {
        return try {
            ImageIO.read(url)
        } catch (e: IOException) {
            null
        }
    }
}