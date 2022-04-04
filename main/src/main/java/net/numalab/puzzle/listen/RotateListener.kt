package net.numalab.puzzle.listen

import net.numalab.puzzle.RotationUtils
import net.numalab.puzzle.map.ImagedMapManager
import org.bukkit.Material
import org.bukkit.entity.ItemFrame
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
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
                val toUpdate =
                    e.rightClicked.world.getNearbyEntitiesByType(ItemFrame::class.java, e.rightClicked.location, 1.0)
                toUpdate.removeIf { it.uniqueId == e.rightClicked.uniqueId }
                for (itemFrame in toUpdate) {
                    updateNotRotate(itemFrame)
                }

                updateRotate(e.rightClicked as ItemFrame)
            }
        }
    }

    private fun updateRotate(itemFrame: ItemFrame) {
        val item = itemFrame.item
        itemRotate(item, itemFrame)
    }

    private fun itemRotate(item: ItemStack, frame: ItemFrame) {
        if (item.type != Material.MAP && item.type != Material.FILLED_MAP) return
        val map = ImagedMapManager.get(item)
        if (map != null) {
            val stacks = ImagedMapManager.getAllStack(map)
            stacks.forEach {
                map.updateStack(it, frame, RotationUtils.add45(frame.rotation))
            }
        }
    }

    private fun updateNotRotate(itemFrame: ItemFrame) {
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
}