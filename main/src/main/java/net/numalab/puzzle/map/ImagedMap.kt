package net.numalab.puzzle.map

import net.numalab.puzzle.puzzle.Piece
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.MapMeta
import java.awt.image.BufferedImage

class ImagedMap(var img: BufferedImage, val piece: Piece) {
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
        // TODO
        val meta = stack.itemMeta
        if (meta is MapMeta) {
            val view = meta.mapView
            if (view != null) {
                val renderer = view.renderers[0]
                if (renderer is ImageMapRenderer) {
                    println("update at $location")
                    renderer.img = drawOverlay()
                }
            }
        } else {
            println("[ERROR]meta is not MapMeta")
        }
    }

    private fun drawOverlay(): BufferedImage {
        return img  // TODO
    }
}