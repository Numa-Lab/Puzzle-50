package net.numalab.puzzle.listen

import net.numalab.puzzle.PuzzlePlugin
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class DropListener(val plugin: PuzzlePlugin) : Listener {
    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    fun onDrop(event: org.bukkit.event.player.PlayerDropItemEvent) {
        plugin.assertion.drop.assert { true }
        updateEmphasize(event.player)
    }

    private fun updateEmphasize(player: org.bukkit.entity.Player) {
        plugin.server.scheduler.runTaskLater(plugin, Runnable {
            plugin.emphasize.updatePlayer(player)
        }, 1)
    }
}