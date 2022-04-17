package net.numalab.puzzle.solved

import com.github.bun133.bukkitfly.component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.numalab.puzzle.PuzzlePlugin
import net.numalab.puzzle.listen.isSolved
import net.numalab.puzzle.map.ImagedMapManager
import org.bukkit.entity.ItemFrame
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerInteractAtEntityEvent

/**
 * 完成後にパズルをいじらせないようにするヤツ
 */
class InteractPrevent(val plugin: PuzzlePlugin) : Listener {
    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onInteract(e: PlayerInteractAtEntityEvent) {
        if (plugin.config.lockPuzzleAfterSolved.value()) {
            if (e.rightClicked is ItemFrame) {
                val item = (e.rightClicked as ItemFrame).item
                if (!item.type.isEmpty) {
                    val imagedMap = ImagedMapManager.get(item)
                    if (imagedMap != null && isSolved(imagedMap.piece.puzzle)) {
                        e.isCancelled = true
                        e.player.sendMessage(text("このパズルは完成しているため、パズルをいじることはできません", NamedTextColor.RED))
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onRemove(e: EntityDamageByEntityEvent) {
        if (plugin.config.lockPuzzleAfterSolved.value()) {
            if (e.entity is ItemFrame) {
                val item = (e.entity as ItemFrame).item
                if (!item.type.isEmpty) {
                    val imagedMap = ImagedMapManager.get(item)
                    if (imagedMap != null && isSolved(imagedMap.piece.puzzle)) {
                        e.isCancelled = true
                        e.damager.sendMessage(text("このパズルは完成しているため、パズルをいじることはできません", NamedTextColor.RED))
                    }
                }
            }
        }
    }
}