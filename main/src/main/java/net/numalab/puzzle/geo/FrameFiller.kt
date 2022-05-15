package net.numalab.puzzle.geo

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.entity.EntityType
import org.bukkit.entity.ItemFrame
import org.bukkit.inventory.ItemStack

class FrameFiller(val startLocation: Location, val width: Int, val height: Int) {
    fun set(piece: List<ItemStack>) = setAll(piece.toTypedArray())
    private fun setAll(piece: Array<ItemStack?>): Boolean {
        if (piece.size > width * height) {
            throw IllegalArgumentException("piece size is too large")
        }

        try {
            for (x in 0 until width) {
                for (z in 0 until height) {
                    val location = startLocation.clone().add(x.toDouble(), .0, z.toDouble())
                    val bottomLocation = location.clone().add(.0, -1.0, .0)
                    if (!bottomLocation.block.isBuildable) {
                        bottomLocation.block.type = Material.STONE
                    }

                    if (!location.block.type.isAir) {
                        location.block.type = Material.AIR
                    }

                    val index = x * height + z
                    setItemFrame(location, piece[index])
                }
            }

            return true
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            return false
        }
    }

    fun placeItemFrame() = setAll(arrayOfNulls<ItemStack?>(width * height))

    private fun setItemFrame(location: Location, content: ItemStack?, isOverWrite: Boolean = true) {
        val i = location.getItemFrame()
        if (i != null) {
            if (isOverWrite || !i.item.type.isItem) {
                i.setItem(content)
                i.setFacingDirection(BlockFace.UP, true)
            } else {
                throw IllegalArgumentException("item frame is already exist")
            }
        } else {
            val itemFrame = location.world.spawnEntity(location, EntityType.ITEM_FRAME)
            itemFrame as ItemFrame
            itemFrame.setItem(content)
            itemFrame.setFacingDirection(BlockFace.UP, true)
        }
    }

    private fun Location.getItemFrame(maxDistance: Double = 0.5 - 0.0625 + 0.01): ItemFrame? {
        return this.world.getNearbyEntitiesByType(ItemFrame::class.java, this.toCenterLocation(), maxDistance).firstOrNull()
    }
}