package net.numalab.puzzle.map

import net.numalab.puzzle.puzzle.Piece
import net.numalab.puzzle.puzzle.PieceSideType
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.MapMeta
import java.awt.image.BufferedImage

class ImagedMap(var img: BufferedImage, val piece: Piece) {
    companion object {
        const val OverlayPixel = 5
    }

    fun get(world: World): ItemStack {
        val map = Bukkit.createMap(world)
        map.renderers.forEach {
            map.removeRenderer(it)
        }

        map.addRenderer(ImageMapRenderer(drawOverlay()))

        val stack = ItemStack(Material.FILLED_MAP)
        stack.editMeta {
            it as MapMeta
            it.mapView = map
        }

        ImagedMapManager.register(stack, this)

        return stack
    }

    /**
     * @param location このMapが入っているItemFrameのLocation、入っていなければnull
     */
    fun updateStack(stack: ItemStack, location: Location?) {
        val meta = stack.itemMeta
        if (meta is MapMeta) {
            val view = meta.mapView
            if (view != null) {
                val renderer = view.renderers[0]
                if (renderer is ImageMapRenderer) {
                    renderer.img = drawOverlay()
                }
            }
        } else {
            println("[ERROR]meta is not MapMeta")
        }
    }

    private fun drawOverlay(): BufferedImage {
        val copy = BufferedImage(img.width, img.height, img.type)
        val g = copy.graphics
        g.drawImage(img, 0, 0, null)
        g.dispose()

        drawUpOverLay(copy)
        drawDownOverLay(copy)
        drawLeftOverLay(copy)
        drawRightOverLay(copy)

        return copy
    }

    private fun drawUpOverLay(img: BufferedImage) {
        drawOverLayInRect(0, 0, img.width, OverlayPixel, img, piece.top)
    }

    private fun drawDownOverLay(img: BufferedImage) {
        drawOverLayInRect(0, img.height - OverlayPixel, img.width, OverlayPixel, img, piece.bottom)
    }

    private fun drawLeftOverLay(img: BufferedImage) {
        drawOverLayInRect(0, 0, OverlayPixel, img.height, img, piece.left)
    }

    private fun drawRightOverLay(img: BufferedImage) {
        drawOverLayInRect(img.width - OverlayPixel, 0, OverlayPixel, img.height, img, piece.right)
    }

    private fun drawOverLayInRect(
        fromX: Int,
        fromY: Int,
        width: Int,
        height: Int,
        img: BufferedImage,
        type: PieceSideType
    ) {
        val c = type.color
        if (c != null) {
            val g = img.graphics
            g.color = c
            g.fillRect(fromX, fromY, width, height)
            g.dispose()
        }
    }
}