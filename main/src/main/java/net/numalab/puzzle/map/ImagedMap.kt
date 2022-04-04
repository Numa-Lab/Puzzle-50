package net.numalab.puzzle.map

import net.numalab.puzzle.RotationUtils
import net.numalab.puzzle.puzzle.Piece
import net.numalab.puzzle.puzzle.PieceSideType
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Rotation
import org.bukkit.World
import org.bukkit.block.BlockFace
import org.bukkit.entity.ItemFrame
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

        map.addRenderer(ImageMapRenderer(drawOverlay(null, Rotation.NONE)))

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
    fun updateStack(stack: ItemStack, frame: ItemFrame?, toRotation: Rotation?) {
        val meta = stack.itemMeta
        if (meta is MapMeta) {
            val view = meta.mapView
            if (view != null) {
                val renderer = view.renderers[0]
                if (renderer is ImageMapRenderer) {
                    renderer.img = drawOverlay(frame, toRotation)
                }
            }
        } else {
            println("[ERROR]meta is not MapMeta")
        }
    }

    private fun getNextItemFrame(frame: ItemFrame, direction: BlockFace): ItemFrame? {
        val location = when (direction) {
            BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST ->
                frame.location.clone().add(direction.direction)
            else -> throw IllegalArgumentException("rotation is not NONE, CLOCKWISE, COUNTER_CLOCKWISE, FLIPPED")
        }

        val re = location.world.getNearbyEntitiesByType(ItemFrame::class.java, location, .1)
        return re.firstOrNull()
    }

    private fun getNextPieceFaceType(from: ItemFrame, to: BlockFace): PieceSideType? {
        val itemFrame = getNextItemFrame(from, to)
        if (itemFrame != null) {
            val stack = itemFrame.item
            val imaged = ImagedMapManager.get(stack)
            if (imaged != null) {
                val flippedDirection = to.oppositeFace
                return ImagedMapManager.getType(itemFrame, flippedDirection)
            }
        }

        return null
    }

    private fun getDirection(base: Rotation, add: Rotation): BlockFace {
        return RotationUtils.rotationToFace(RotationUtils.addRotation(base, add))
    }

    private fun isUpOverlay(itemFrame: ItemFrame?, toRotation: Rotation): Boolean {
        if (itemFrame != null) {
            val direction = getDirection(Rotation.NONE, toRotation)
            val sideType = getNextPieceFaceType(itemFrame, direction) ?: return true
            return sideType != piece.top
        }
        return true
    }

    private fun isRightOverlay(itemFrame: ItemFrame?, toRotation: Rotation): Boolean {
        if (itemFrame != null) {
            val direction = getDirection(Rotation.CLOCKWISE_45, toRotation)
            val sideType = getNextPieceFaceType(itemFrame, direction) ?: return true
            return sideType != piece.right
        }
        return true
    }

    private fun isDownOverlay(itemFrame: ItemFrame?, toRotation: Rotation): Boolean {
        if (itemFrame != null) {
            val direction = getDirection(Rotation.CLOCKWISE, toRotation)
            val sideType = getNextPieceFaceType(itemFrame, direction) ?: return true
            return sideType != piece.bottom
        }
        return true
    }

    private fun isLeftOverlay(itemFrame: ItemFrame?, toRotation: Rotation): Boolean {
        if (itemFrame != null) {
            val direction = getDirection(Rotation.CLOCKWISE_135, toRotation)
            val sideType = getNextPieceFaceType(itemFrame, direction) ?: return true
            return sideType != piece.left
        }
        return true
    }


    private fun drawOverlay(frame: ItemFrame?, toRotation: Rotation?): BufferedImage {
        val rot = toRotation ?: Rotation.NONE
        val copy = BufferedImage(img.width, img.height, img.type)
        val g = copy.graphics
        g.drawImage(img, 0, 0, null)
        g.dispose()

        if (isUpOverlay(frame, rot)) {
            drawUpOverLay(copy)
        }
        if (isDownOverlay(frame, rot)) {
            drawDownOverLay(copy)
        }
        if (isLeftOverlay(frame, rot)) {
            drawLeftOverLay(copy)
        }
        if (isRightOverlay(frame, rot)) {
            drawRightOverLay(copy)
        }

        return copy
    }

    //<editor-fold defaultstate="collapsed" desc="Draw Overlays">
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
    //</editor-fold>

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