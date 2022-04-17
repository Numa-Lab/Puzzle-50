package net.numalab.puzzle.listen

import net.numalab.puzzle.PuzzlePlugin
import net.numalab.puzzle.map.ImagedMapManager
import net.numalab.puzzle.map.assign.MapAssigner
import org.bukkit.entity.ItemFrame
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEntityEvent

class RotateListener(val plugin: PuzzlePlugin) : Listener {
    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    fun onClick(e: PlayerInteractEntityEvent) {
        if (e.isCancelled) return
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