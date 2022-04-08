package net.numalab.puzzle.listen

import net.numalab.puzzle.map.ImagedMapManager
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.plugin.Plugin

class PickUpListener(plugin: Plugin) : Listener {
    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    fun onPickUp(e: EntityPickupItemEvent) {
        if (e.entityType == EntityType.PLAYER && (e.item.itemStack.type == Material.MAP || e.item.itemStack.type == Material.FILLED_MAP)) {
            val map = ImagedMapManager.get(e.item.itemStack)
            if (map != null) {
                val stacks = ImagedMapManager.getAllStack(map)
                stacks.forEach {
                    map.updateStack(it, null, null)
                }

                checkSolved(map.piece.puzzle, e.entity.location, e.entity as Player)
            }
        }
    }
}