package net.numalab.puzzle.listen

import net.numalab.puzzle.map.ImagedMapManager
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.ItemFrame
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityInteractEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin

class PlaceListener(plugin: JavaPlugin) : Listener {
    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    fun onPlace(e: PlayerInteractEntityEvent) {
        val en = e.rightClicked
        if (en is ItemFrame) {
            val item = en.item
            if (item.type.isEmpty) {
                val mainHand = e.player.inventory.itemInMainHand
                item(mainHand, en.location)

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