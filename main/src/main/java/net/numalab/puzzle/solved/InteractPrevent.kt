package net.numalab.puzzle.solved

import com.github.bun133.bukkitfly.component.text
import com.github.bun133.bukkitfly.listen.listen
import net.kyori.adventure.text.format.NamedTextColor
import net.numalab.puzzle.PuzzlePlugin
import net.numalab.puzzle.listen.RemoveListener
import net.numalab.puzzle.listen.RotateListener
import net.numalab.puzzle.listen.checkSolved
import net.numalab.puzzle.listen.isSolved
import net.numalab.puzzle.map.ImagedMapManager
import org.bukkit.entity.ItemFrame
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerInteractAtEntityEvent

/**
 * 完成後にパズルをいじらせないようにするヤツ
 */
class InteractPrevent(
    val plugin: PuzzlePlugin,
    private val rotateListener: RotateListener,
    val removeListener: RemoveListener
) : Listener {
    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onInteract(e: PlayerInteractAtEntityEvent) {
        var isCancelled = false
        if (plugin.config.lockPuzzleAfterSolved.value()) {
            if (e.rightClicked is ItemFrame) {
                val item = (e.rightClicked as ItemFrame).item
                if (!item.type.isEmpty) {
                    val imagedMap = ImagedMapManager.get(item)
                    if (imagedMap != null && isSolved(imagedMap.piece.puzzle)) {
                        // Check
                        checkSolved(e.rightClicked as ItemFrame, e.player, plugin)

                        e.isCancelled = true
                        isCancelled = true
                        val beforeRotation = (e.rightClicked as ItemFrame).rotation

                        plugin.server.scheduler.runTaskLater(plugin, Runnable {
                            (e.rightClicked as ItemFrame).rotation = beforeRotation
                            e.player.sendMessage(text("このパズルは完成しているため、パズルをいじることはできません", NamedTextColor.RED))
                        }, 1)
                    }
                }
            }
        }

        if (!isCancelled) {
            rotateListener.onClick(e)
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onRemove(e: EntityDamageByEntityEvent) {
        var isCancelled = false
        val p = e.damager
        if (p is Player) {
            if (plugin.config.lockPuzzleAfterSolved.value()) {
                if (e.entity is ItemFrame) {
                    val item = (e.entity as ItemFrame).item
                    if (!item.type.isEmpty) {
                        val imagedMap = ImagedMapManager.get(item)
                        if (imagedMap != null && isSolved(imagedMap.piece.puzzle)) {
                            // Check
                            checkSolved(e.entity as ItemFrame, p, plugin)

                            e.isCancelled = true
                            isCancelled = true
                            e.damager.sendMessage(text("このパズルは完成しているため、パズルをいじることはできません", NamedTextColor.RED))
                        }
                    }
                }
            }
        }


        if (!isCancelled) {
            removeListener.onRemove(e)
        }
    }
}