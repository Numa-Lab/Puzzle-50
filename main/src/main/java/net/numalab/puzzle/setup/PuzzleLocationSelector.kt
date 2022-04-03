package net.numalab.puzzle.setup

import net.numalab.puzzle.geo.FrameFiller
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import java.util.UUID

class PuzzleLocationSelector(plugin: JavaPlugin) : Listener {
    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    private val queue = mutableMapOf<UUID, Pair<List<ItemStack>, Pair<Int, Int>>>()

    @EventHandler
    fun onRightClick(e: PlayerInteractEvent) {
        if (e.action == Action.LEFT_CLICK_BLOCK || e.action == Action.RIGHT_CLICK_BLOCK) {
            val en = queue[e.player.uniqueId] ?: return
            val b =
                FrameFiller(e.clickedBlock!!.location.add(.0, 1.0, .0), en.second.first, en.second.second).set(en.first)
            if (b) {
                e.player.sendMessage("一部のピースの設置に失敗しました。平らな場所でもう一度設定してください。")
            } else {
                e.player.sendMessage("パズルの開始位置を設定しました")
                queue.remove(e.player.uniqueId)
            }
        }
    }

    fun addQueue(uuid: UUID, item: List<ItemStack>, width: Int, height: Int) {
        queue[uuid] = Pair(item, Pair(width, height))
    }


    fun reset() {
        queue.clear()
    }
}