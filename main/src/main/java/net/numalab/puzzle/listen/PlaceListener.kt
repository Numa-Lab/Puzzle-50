package net.numalab.puzzle.listen

import net.numalab.puzzle.PuzzlePlugin
import net.numalab.puzzle.map.ImagedMapManager
import net.numalab.puzzle.map.assign.MapAssigner
import org.bukkit.Material
import org.bukkit.Rotation
import org.bukkit.entity.ItemFrame
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin

class PlaceListener(val plugin: PuzzlePlugin) : Listener {
    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    fun onPlace(e: PlayerInteractEntityEvent) {
        val en = e.rightClicked
        if (en is ItemFrame) {
            val item = en.item
            if (item.type.isEmpty) {
                plugin.server.scheduler.runTaskLater(plugin, Runnable {
                    val toUpdate = en.world.getNearbyEntitiesByType(ItemFrame::class.java, en.location, 1.0)
                    toUpdate.forEach {
                        update(it)
                    }

                    checkSolved(en, e.player,plugin)
                    updateEmphasize(en, e.player)
                }, 1)
            }
        }
    }

    private fun updateEmphasize(itemFrame: ItemFrame, player: Player) {
        plugin.emphasize.updatePlayer(player)
    }
}