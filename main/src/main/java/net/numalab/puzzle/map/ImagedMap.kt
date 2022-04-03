package net.numalab.puzzle.map

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.MapMeta
import java.awt.image.BufferedImage

class ImagedMap(val img: BufferedImage) {
    fun get(world:World): ItemStack {
        val map = Bukkit.createMap(world)
        map.renderers.forEach {
            map.removeRenderer(it)
        }

        map.addRenderer(ImageMapRenderer(img))

        val stack = ItemStack(Material.FILLED_MAP)
        stack.editMeta {
            it as MapMeta
            it.mapView = map
        }

        return stack
    }
}