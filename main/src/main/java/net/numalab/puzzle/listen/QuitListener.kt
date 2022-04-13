package net.numalab.puzzle.listen

import com.github.bun133.bukkitfly.component.plus
import com.github.bun133.bukkitfly.component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.numalab.puzzle.PuzzlePlugin
import net.numalab.puzzle.map.ImagedMapManager
import net.numalab.puzzle.map.assign.MapAssigner
import net.numalab.puzzle.setup.QuitSetting
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class QuitListener(val plugin: PuzzlePlugin) : Listener {
    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    fun onQuit(e: PlayerQuitEvent) {
        val stacks = ImagedMapManager.getAllStack().filter { MapAssigner.getAssigned(it) == e.player.uniqueId }
        val toConsider = stacks.associateWith { ImagedMapManager.get(it)!! }
        var isAssigned = false
        toConsider.forEach {
            val mode = it.value.piece.puzzle.attributes.find { v -> v is QuitSetting }
            if (mode != null) {
                if (mode == QuitSetting.AssignToAll) {
                    val toAssign =
                        plugin.config.players().filter { p -> p.uniqueId != e.player.uniqueId }.randomOrNull()
                    if (toAssign != null) {
                        MapAssigner.assign(it.key, toAssign, true)
                        isAssigned = true
                    }
                } else if (mode == QuitSetting.None) {
                    isAssigned = true
                }
            }
        }
        if (!isAssigned) {
            Bukkit.broadcast(e.player.displayName() + text("の一部のピースは割り当てる相手が見つからなかったため、割り当ては変更されませんでした"))
        }

        val toRemove = e.player.inventory.contents.filterNotNull().filter { toConsider.containsKey(it) }

        val cantRemoved = e.player.inventory.removeItemAnySlot(*toConsider.keys.toTypedArray()).values.toList()

        println("toConsider: ${toConsider.size}")
        println("toRemove: ${toRemove.size}")
        println("cantRemoved: ${cantRemoved.size}")

        if (toConsider.size != cantRemoved.size + toRemove.size || cantRemoved.size == toRemove.size) {
            Bukkit.broadcast(text("正常にインベントリからピースを削除できませんでした", NamedTextColor.RED))
            Bukkit.broadcast(text("ピースが${e.player.name}のインベントリで増殖した可能性があります", NamedTextColor.RED))
        }

        toConsider.filter { cantRemoved.contains(it.key) }.forEach { (stack, _) ->
            e.player.location.world.dropItem(e.player.location, stack)
        }
    }
}