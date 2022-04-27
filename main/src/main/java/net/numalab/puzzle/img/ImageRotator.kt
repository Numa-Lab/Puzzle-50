package net.numalab.puzzle.img

import java.awt.image.BufferedImage

class ImageRotator {
    companion object {
        // img must be square
        fun rotateTimes(img: BufferedImage, times: Int): BufferedImage {
            val w = img.width
            val h = img.height
            val bImg = BufferedImage(w, h, img.type)

            repeat(times % 4) {
                rotate(bImg)
            }

            return bImg
        }

        // Overwrite img
        private fun rotate(img: BufferedImage): BufferedImage {
            val g = img.graphics
            val w = img.width
            val h = img.height
            g.drawImage(img, w, 0, 0, h, 0, 0, w, h, null)
            g.dispose()
            return img
        }
    }
}