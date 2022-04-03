package net.numalab.puzzle.listen

import net.numalab.puzzle.map.ImagedMapManager
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.ItemFrame
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin

class RotateListener(plugin: Plugin) : Listener {
    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    fun onClick(e: PlayerInteractEntityEvent) {
        if (e.rightClicked is ItemFrame) {
            val item = (e.rightClicked as ItemFrame).item
            if (!item.type.isEmpty) {
                // rotated
                item(item, e.rightClicked.location)
            }
        }
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