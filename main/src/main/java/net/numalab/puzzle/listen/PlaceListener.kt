package net.numalab.puzzle.listen

import net.numalab.puzzle.map.ImagedMapManager
import org.bukkit.Material
import org.bukkit.Rotation
import org.bukkit.entity.ItemFrame
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEntityEvent
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
                itemRotate(mainHand, en)

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
        itemNotRotate(item, itemFrame)
    }

    private fun itemNotRotate(item: ItemStack, frame: ItemFrame) {
        if (item.type != Material.MAP && item.type != Material.FILLED_MAP) return
        val map = ImagedMapManager.get(item)
        if (map != null) {
            val stacks = ImagedMapManager.getAllStack(map)
            stacks.forEach {
                map.updateStack(it, frame, frame.rotation)
            }
        }
    }

    private fun itemRotate(item: ItemStack, frame: ItemFrame) {
        if (item.type != Material.MAP && item.type != Material.FILLED_MAP) return
        val map = ImagedMapManager.get(item)
        if (map != null) {
            val stacks = ImagedMapManager.getAllStack(map)
            stacks.forEach {
                map.updateStack(it, frame, Rotation.NONE)
            }
        }
    }
}