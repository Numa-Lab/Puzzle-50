package net.numalab.puzzle.listen

import net.numalab.puzzle.map.ImagedMapManager
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.ItemFrame
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin

class RemoveListener(plugin: JavaPlugin) : Listener {
    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    fun onRemove(e: EntityDamageByEntityEvent) {
        if (e.entityType == EntityType.ITEM_FRAME) {
            val en = e.entity
            if (en is ItemFrame) {
                val toUpdate = en.world.getNearbyEntitiesByType(ItemFrame::class.java, en.location, 1.0)
                toUpdate.removeIf { it.uniqueId == en.uniqueId }
                toUpdate.forEach {
                    update(it)
                }
            }
        }
    }

    private fun update(itemFrame: ItemFrame) {
        val item = itemFrame.item
        item(item, itemFrame.location)
    }

    private fun item(item: ItemStack, location: Location) {
        if (item.type != Material.MAP && item.type != Material.FILLED_MAP) return
        val map = ImagedMapManager.get(item)
        if (map != null) {
            val stacks = ImagedMapManager.getAllStack(map)
            stacks.forEach {
                map.updateStack(it, location)
            }
        }
    }
}