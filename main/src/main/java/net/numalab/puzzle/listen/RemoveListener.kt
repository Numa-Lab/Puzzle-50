package net.numalab.puzzle.listen

import net.numalab.puzzle.map.ImagedMapManager
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Rotation
import org.bukkit.entity.EntityType
import org.bukkit.entity.ItemFrame
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin

class RemoveListener(val plugin: JavaPlugin) : Listener {
    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    fun onRemove(e: EntityDamageByEntityEvent) {
        if (e.isCancelled) return
        if (e.entityType == EntityType.ITEM_FRAME && e.damager.type == EntityType.PLAYER) {
            val en = e.entity
            if (en is ItemFrame) {
                val toUpdate = en.world.getNearbyEntitiesByType(ItemFrame::class.java, en.location, 1.0)
                toUpdate.removeIf { it.uniqueId == en.uniqueId }
                plugin.server.scheduler.runTaskLater(plugin, Runnable {
                    toUpdate.forEach {
                        update(it)
                    }
                    itemNot(en.item)

                    checkSolved(en,e.damager as Player)
                },1L)
            }
        }
    }

    private fun itemNot(item: ItemStack) {
        if (item.type != Material.MAP && item.type != Material.FILLED_MAP) return
        val map = ImagedMapManager.get(item)
        if (map != null) {
            val stacks = ImagedMapManager.getAllStack(map)
            stacks.forEach {
                map.updateStack(it, null, null)
            }
        }
    }
}