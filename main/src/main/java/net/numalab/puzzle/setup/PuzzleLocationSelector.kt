package net.numalab.puzzle.setup

import net.numalab.puzzle.geo.FrameFiller
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.plugin.java.JavaPlugin
import java.util.UUID

class PuzzleLocationSelector(plugin: JavaPlugin) : Listener {
    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    private val queue = mutableMapOf<UUID, (Player, Location) -> Unit>()

    @EventHandler
    fun onRightClick(e: PlayerInteractEvent) {
        if (e.action == Action.LEFT_CLICK_BLOCK || e.action == Action.RIGHT_CLICK_BLOCK) {
            queue.remove(e.player.uniqueId)?.let {
                it(e.player,e.clickedBlock!!.location)
            }
        }
    }

    fun addQueue(uuid: UUID, callBack: (Player, Location) -> Unit) {
        queue[uuid] = callBack
    }


    fun reset() {
        queue.clear()
    }
}