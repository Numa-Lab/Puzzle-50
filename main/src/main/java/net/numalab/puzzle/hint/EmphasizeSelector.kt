package net.numalab.puzzle.hint

import net.numalab.puzzle.PuzzlePlugin
import net.numalab.puzzle.map.ImagedMapManager
import org.bukkit.entity.ItemFrame
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEntityEvent
import java.util.UUID

class EmphasizeSelector(val plugin: PuzzlePlugin) : Listener {
    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    val playerQueue = mutableListOf<UUID>()

    @EventHandler(priority = EventPriority.HIGH)
    fun onRotateItemFrame(e: PlayerInteractEntityEvent) {
        if (playerQueue.contains(e.player.uniqueId)) {
            if (e.rightClicked is ItemFrame) {
                val item = (e.rightClicked as ItemFrame).item
                if (!item.type.isEmpty) {
                    val map = ImagedMapManager.get(item)
                    if (map != null) {
                        plugin.emphasize.puzzle.value = map.piece.puzzle
                        plugin.emphasize.isEnabled.value = true
                        playerQueue.remove(e.player.uniqueId)
                        e.isCancelled = true
                    }
                }
            }
        }
    }

    fun reset() {
        playerQueue.clear()
    }
}