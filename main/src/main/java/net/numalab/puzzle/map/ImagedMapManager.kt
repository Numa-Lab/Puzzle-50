package net.numalab.puzzle.map

import net.numalab.puzzle.RotationUtils
import net.numalab.puzzle.puzzle.PieceSideType
import org.bukkit.NamespacedKey
import org.bukkit.block.BlockFace
import org.bukkit.entity.ItemFrame
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

class ImagedMapManager {
    companion object {
        private val imagedMap = mutableMapOf<String, ImagedMap>()
        private val stackMap = mutableMapOf<String, MutableList<ItemStack>>()
        private val nameSpacedKey = NamespacedKey("puzzle", "stackmarker")

        fun register(itemStack: ItemStack, map: ImagedMap) {
            val meta = itemStack.itemMeta
            if (meta != null) {
                meta.persistentDataContainer.set(nameSpacedKey, PersistentDataType.STRING, map.piece.uuid.toString())
                imagedMap[map.piece.uuid.toString()] = map
                addStack(map.piece.uuid.toString(), itemStack)
                itemStack.itemMeta = meta
            }
        }

        private fun addStack(uuid: String, itemStack: ItemStack) {
            val e = stackMap[uuid]
            if (e != null) {
                e.add(itemStack)
            } else {
                stackMap[uuid] = mutableListOf(itemStack)
            }
        }

        fun get(itemStack: ItemStack): ImagedMap? {
            val meta = itemStack.itemMeta
            if (meta != null) {
                val uuid = meta.persistentDataContainer.get(nameSpacedKey, PersistentDataType.STRING)
                if (uuid != null) {
                    return imagedMap[uuid]
                }
            }
            return null
        }

        fun getAllStack(map: ImagedMap): List<ItemStack> {
            return stackMap[map.piece.uuid.toString()]?.toMutableList() ?: listOf()
        }

        fun reset() {
            imagedMap.clear()
        }

        fun getType(itemFrame: ItemFrame, direction: BlockFace): PieceSideType? {
            val map = get(itemFrame.item) ?: return null
            val dir = RotationUtils.decrementRotation(direction, itemFrame.rotation)
            return map.piece.get(dir)
        }
    }
}