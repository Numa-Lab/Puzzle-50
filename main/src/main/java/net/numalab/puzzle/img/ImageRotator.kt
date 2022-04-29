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
            return bImg.rotateClockWise((times % 4) * 0.5 * Math.PI)
        }

        private fun BufferedImage.rotateClockWise(rad: Double): BufferedImage {
            val width: Int = 128
            val height: Int = 128
            val dest = BufferedImage(height, width, this.type)
            val graphics2D = dest.createGraphics()
            graphics2D.translate((height - width) / 2, (height - width) / 2)
            graphics2D.rotate(rad, (height / 2).toDouble(), (width / 2).toDouble())
            graphics2D.drawRenderedImage(this, null)
            return dest
        }
    }
}