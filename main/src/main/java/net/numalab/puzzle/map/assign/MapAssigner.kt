package net.numalab.puzzle.map.assign

import com.github.bun133.bukkitfly.component.text
import com.github.bun133.bukkitfly.stack.rename
import net.kyori.adventure.text.Component
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.UUID

class MapAssigner private constructor() {
    companion object {
        private val Key = NamespacedKey("puzzle", "map-assigner")
        private val assigned = mutableMapOf<UUID, UUID>()

        /**
         * @return if success
         */
        fun assign(map: ItemStack, player: Player, changeItemName: Boolean): Boolean {
            unassign(map)

            val uuid = UUID.randomUUID()
            var result = false
            map.editMeta {
                it.persistentDataContainer.set(Key, PersistentDataType.STRING, uuid.toString())
                result = true
            }

            if (changeItemName) {
                map.editMeta {
                    it.setDisplayName("${player.name}のピース")
                }
            }

            if (result) {
                assigned[uuid] = player.uniqueId
            }
            return result
        }

        fun unassign(map: ItemStack): Boolean {
            var result = false
            map.editMeta {
                val value = it.persistentDataContainer.get(Key, PersistentDataType.STRING)
                if (value != null) {
                    val uuid: UUID
                    try {
                        uuid = UUID.fromString(value)
                    } catch (e: java.lang.IllegalArgumentException) {
                        return@editMeta
                    }
                    assigned.remove(uuid)
                }
                it.persistentDataContainer.remove(Key)
                result = true
            }
            return result
        }

        fun getAssigned(map: ItemStack): UUID? {
            var uuidStr: String? = null
            map.editMeta {
                uuidStr = it.persistentDataContainer.get(Key, PersistentDataType.STRING)
            }
            return if (uuidStr != null) {
                try {
                    val uuid = UUID.fromString(uuidStr)
                    assigned[uuid]
                } catch (e: IllegalArgumentException) {
                    null
                }
            } else {
                null
            }
        }
    }
}