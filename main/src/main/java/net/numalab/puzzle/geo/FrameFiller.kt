package net.numalab.puzzle.geo

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.ItemFrame
import org.bukkit.inventory.ItemStack

class FrameFiller(val startLocation: Location, val width: Int, val height: Int) {
    fun set(piece: List<ItemStack>): Boolean {
        if (piece.size > width * height) {
            throw IllegalArgumentException("piece size is too large")
        }

        var isFailed = false

        for (x in 0 until width) {
            for (z in 0 until height) {
                val location = startLocation.clone().add(x.toDouble(), .0, z.toDouble())
                val bottomLocation = location.clone().add(.0, -1.0, .0)
                if (!bottomLocation.block.isBuildable) {
                    bottomLocation.block.type = Material.STONE
                }
                val index = x * height + z
                try {
                    setItemFrame(location, piece[index])
                } catch (e: IllegalArgumentException) {
                    // 握りつぶします
                    isFailed = true
                }
            }
        }

        return isFailed
    }

    private fun setItemFrame(location: Location, content: ItemStack) {
        val itemFrame = location.world.spawnEntity(location, EntityType.ITEM_FRAME)
        itemFrame as ItemFrame
        itemFrame.setItem(content)
    }
}