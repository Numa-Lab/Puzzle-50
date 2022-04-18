package net.numalab.puzzle.listen

import com.github.bun133.bukkitfly.component.plus
import com.github.bun133.bukkitfly.component.text
import com.github.bun133.bukkitfly.inventory.player.removeItemAnySlotForce
import com.github.bun133.bukkitfly.stack.addOrDrop
import net.kyori.adventure.text.format.NamedTextColor
import net.numalab.puzzle.PuzzlePlugin
import net.numalab.puzzle.map.ImagedMapManager
import net.numalab.puzzle.map.assign.MapAssigner
import net.numalab.puzzle.setup.QuitSetting
import net.numalab.puzzle.solved.getTeamSession
import org.bukkit.Bukkit
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
        val assignedMapStacks =
            ImagedMapManager.getAllStack().filter { MapAssigner.getAssigned(it) == e.player.uniqueId }
        val assignedImagedMap = assignedMapStacks.associateWith { ImagedMapManager.get(it)!! }
        val assignedPlayer = e.player
        var isAssigned = assignedImagedMap.isEmpty()
        assignedImagedMap.forEach {
            val mode = it.value.piece.puzzle.attributes.find { v -> v is QuitSetting }
            if (mode != null) {
                when (mode) {
                    QuitSetting.AssignToAll -> {
                        val teamSessionData = it.value.piece.puzzle.getTeamSession()
                        val toAssignTargetPlayer = teamSessionData?.teams ?: Bukkit.getOnlinePlayers()


                        val toAssignPlayer =
                            toAssignTargetPlayer.filter { p -> p.uniqueId != e.player.uniqueId }.randomOrNull()
                        if (toAssignPlayer != null) {
                            assignedPlayer.inventory.removeItemAnySlot(it.key)
                            MapAssigner.assign(it.key, toAssignPlayer, true)
                            toAssignPlayer.inventory.addOrDrop(it.key)
                            isAssigned = true
                        }
                    }

                    QuitSetting.None -> {
                        // Do Nothing
                        isAssigned = true
                    }
                }
            }
        }

        if (!isAssigned) {
            Bukkit.broadcast(e.player.displayName() + text("の一部のピースは割り当てる相手が見つからなかったため、割り当ては変更されませんでした"))
        }
    }
}