package net.numalab.puzzle.img

import java.awt.image.BufferedImage


class ImageRotator {
    companion object {
        // img must be square,Rotating the image anti-clockwise
        fun rotateTimes(img: BufferedImage, times: Int): BufferedImage {
            val w = img.width
            val h = img.height
            val bImg = BufferedImage(w, h, img.type)
            bImg.graphics.drawImage(img, 0, 0, w, h, null)
            return bImg.rotateClockWise(Math.toRadians((times % 4) * 90.0))
        }

        private fun BufferedImage.rotateClockWise(rad: Double): BufferedImage {
            val width: Int = 128
            val height: Int = 128
            val dest = BufferedImage(height, width, this.type)
            val graphics2D = dest.createGraphics()
            graphics2D.rotate(rad, width / 2.0, height / 2.0)
            graphics2D.drawImage(this, 0, 0, this.width, this.height, null)
            return dest
        }
    }
}