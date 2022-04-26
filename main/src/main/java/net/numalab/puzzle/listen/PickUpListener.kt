package net.numalab.puzzle.listen

import com.github.bun133.bukkitfly.component.plus
import com.github.bun133.bukkitfly.component.text
import com.github.bun133.bukkitfly.entity.human.kill
import net.kyori.adventure.text.Component
import net.numalab.puzzle.PuzzlePlugin
import net.numalab.puzzle.map.ImagedMapManager
import net.numalab.puzzle.map.assign.MapAssigner
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import java.util.UUID

class PickUpListener(val plugin: PuzzlePlugin) : Listener {
    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    fun onPickUp(e: EntityPickupItemEvent) {
        if (e.entityType == EntityType.PLAYER && (e.item.itemStack.type == Material.MAP || e.item.itemStack.type == Material.FILLED_MAP)) {
            updateEmphasize(e.entity as Player)

            val assigned = MapAssigner.getAssigned(e.item.itemStack)
            if (assigned != null && assigned != e.entity.uniqueId && (e.entity as Player).gameMode == plugin.config.targetGameMode.value()) {
                // 他の人のマップを拾った
                plugin.assertion.pickUp.assert { true }
                e.isCancelled = true
            }

            val map = ImagedMapManager.get(e.item.itemStack)
            if (map != null) {
                val stacks = ImagedMapManager.getAllStack(map)
                stacks.forEach {
                    map.updateStack(it, null, null)
                }

                checkSolved(map.piece.puzzle, e.entity.location, e.entity as Player, plugin)
                plugin.assertion.pickUp.assert { true }
            }
        }
    }

    private fun deathComponent(toDeath: HumanEntity, assigned: UUID): Component {
        val assignedPlayer = plugin.server.getPlayer(assigned)
        return if (assignedPlayer != null) {
            (toDeath as Player).displayName() + text("は") + assignedPlayer.displayName() + text("のピースを拾ったので死亡した。")
        } else {
            (toDeath as Player).displayName() + text("は他人のピースを拾ったので死亡した。")
        }
    }

    private fun updateEmphasize(player: Player) {
        plugin.server.scheduler.runTaskLater(plugin, Runnable {
            plugin.emphasize.updatePlayer(player)
        }, 1)
    }
}