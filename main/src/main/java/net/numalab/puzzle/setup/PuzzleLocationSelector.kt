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

    private val queue = mutableMapOf<UUID, Pair<(Player, Location, Int) -> Boolean, Int>>()

    @EventHandler
    fun onRightClick(e: PlayerInteractEvent) {
        if (e.action == Action.LEFT_CLICK_BLOCK || e.action == Action.RIGHT_CLICK_BLOCK) {
            queue.remove(e.player.uniqueId)?.let {
                val to = it.second - 1
                val result = it.first(e.player, e.clickedBlock!!.location, to)
                if (result) {
                    if (to > 0) {
                        queue[e.player.uniqueId] = it.copy(second = to)
                    }
                } else {
                    queue[e.player.uniqueId] = it
                }
            }
        }
    }

    fun addQueue(uuid: UUID, times: Int, callBack: (Player, Location, Int) -> Boolean) {
        queue[uuid] = callBack to times
    }


    fun reset() {
        queue.clear()
    }
}