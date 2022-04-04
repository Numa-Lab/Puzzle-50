package net.numalab.puzzle.listen

import net.numalab.puzzle.map.ImagedMapManager
import org.bukkit.Material
import org.bukkit.entity.ItemFrame
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin

class RotateListener(val plugin: Plugin) : Listener {
    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    fun onClick(e: PlayerInteractEntityEvent) {
        if (e.rightClicked is ItemFrame) {
            val item = (e.rightClicked as ItemFrame).item
            if (!item.type.isEmpty) {
                // rotated
                val toUpdate =
                    e.rightClicked.world.getNearbyEntitiesByType(ItemFrame::class.java, e.rightClicked.location, 1.0)
                plugin.server.scheduler.runTaskLater(plugin, Runnable {
                    for (itemFrame in toUpdate) {
                        update(itemFrame)
                    }

                    checkSolved(e.rightClicked as ItemFrame, e.player)
                }, 1)
            }
        }
    }

}