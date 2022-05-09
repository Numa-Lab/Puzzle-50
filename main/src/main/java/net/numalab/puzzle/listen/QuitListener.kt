package net.numalab.puzzle.listen

import com.github.bun133.bukkitfly.component.plus
import com.github.bun133.bukkitfly.component.text
import com.github.bun133.bukkitfly.stack.addOrDrop
import net.numalab.puzzle.PuzzlePlugin
import net.numalab.puzzle.map.ImagedMap
import net.numalab.puzzle.map.ImagedMapManager
import net.numalab.puzzle.map.assign.MapAssigner
import net.numalab.puzzle.setup.QuitSetting
import net.numalab.puzzle.team.TeamSessionData
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack

class QuitListener(val plugin: PuzzlePlugin) : Listener {
    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    fun onQuit(e: PlayerQuitEvent) {
        var isNotProcessed = false
        e.player.inventory.contents
            .filterNotNull()
            .toList()
            .mapNotNull {
                val i = ImagedMapManager.get(it)
                if (i != null) {
                    i to it
                } else {
                    null
                }
            }
            .forEach { pair ->
                // remove item from inventory
                e.player.inventory.remove(pair.second)

                if (pair.first.toReAssign()) {
                    // re-assign item
                    val toAssign = toAssignPlayer(pair.first, e.player)
                    if (toAssign != null) {
                        MapAssigner.assign(pair.second, toAssign, true)
                        toAssign.inventory.addOrDrop(pair.second)
                    } else {
                        isNotProcessed = true
                    }
                } else {
                    // drop item
                    e.player.world.dropItem(e.player.location, pair.second)
                }
            }


        if (isNotProcessed) {
            Bukkit.broadcast(e.player.displayName() + text("の一部のピースは割り当てる相手が見つからなかったため、割り当ては変更されませんでした"))
        }

        plugin.assertion.quit.assert { !isNotProcessed }
    }

    private fun ItemStack.isPiece() = ImagedMapManager.get(this) != null

    private fun ImagedMap.quitSetting(): QuitSetting? {
        return this.piece.puzzle.attributes.filterIsInstance(QuitSetting::class.java).firstOrNull()
    }

    private fun ImagedMap.toReAssign(): Boolean {
        val mode = quitSetting()
        if (mode != null) {
            return mode == QuitSetting.AssignToAll
        }

        return false
    }

    private fun ImagedMap.teamSession(): TeamSessionData? {
        return this.piece.puzzle.attributes.filterIsInstance(TeamSessionData::class.java).firstOrNull()
    }

    private fun toAssignPlayer(map: ImagedMap, except: Player): Player? {
        val from = map.teamSession()?.teams ?: plugin.config.players()
        return from.filter { it.uniqueId != except.uniqueId }.randomOrNull()
    }
}