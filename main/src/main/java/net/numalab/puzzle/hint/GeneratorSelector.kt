package net.numalab.puzzle.hint

import com.github.bun133.bukkitfly.component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.numalab.puzzle.PuzzlePlugin
import net.numalab.puzzle.map.ImagedMap
import net.numalab.puzzle.map.ImagedMapManager
import net.numalab.puzzle.puzzle.Puzzle
import org.bukkit.Location
import org.bukkit.entity.ItemFrame
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEntityEvent
import java.util.*

class GeneratorSelector(val plugin: PuzzlePlugin) : Listener {
    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    val playerQueue = mutableListOf<UUID>()
    private val generatorQueue = mutableMapOf<UUID, ImagedMap>()

    @EventHandler(priority = EventPriority.HIGH)
    fun onRotateItemFrame(e: PlayerInteractEntityEvent) {
        if (e.player.uniqueId in playerQueue) {
            if (e.rightClicked is ItemFrame) {
                val item = (e.rightClicked as ItemFrame).item
                if (!item.type.isEmpty) {
                    val map = ImagedMapManager.get(item)
                    if (map != null) {
                        e.player.sendMessage(text("パズルを選択しました", NamedTextColor.GREEN))
                        generatorQueue[e.player.uniqueId] = map
                        playerQueue.remove(e.player.uniqueId)
                        e.isCancelled = true
                    }
                }
            }
        }
    }

    fun gen(p: Player, x: Int, y: Int): Boolean {
        val map = generatorQueue[p.uniqueId]
        if (map != null) {
            return plugin.generator.genToPlayer(map.piece.puzzle, x, y, p)
        }
        return false
    }

    fun remove(uuid: UUID) {
        playerQueue.remove(uuid)
        generatorQueue.remove(uuid)
    }

    fun reset() {
        playerQueue.clear()
        generatorQueue.clear()
    }

    fun isIn(uuid: UUID): Boolean {
        return uuid in playerQueue || uuid in generatorQueue.keys
    }
}