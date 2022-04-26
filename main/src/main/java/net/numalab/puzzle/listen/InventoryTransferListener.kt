package net.numalab.puzzle.listen

import net.numalab.puzzle.PuzzlePlugin
import net.numalab.puzzle.map.assign.MapAssigner
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryMoveItemEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory

class InventoryTransferListener(val plugin: PuzzlePlugin) : Listener {
    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    fun onTransfer(e: InventoryClickEvent) {
        val p = e.whoClicked as Player
        if (p.gameMode == plugin.config.targetGameMode.value()) {
            (e.view.bottomInventory as? PlayerInventory)?.let { playerInventory ->
                val toReturn = e.view.topInventory

                plugin.server.scheduler.runTaskLater(plugin, Runnable {
                    val r = checkInventory(p, playerInventory, toReturn)
                    plugin.assertion.transfer.assert { r }
                }, 1)
            }
        }
    }

    private fun checkInventory(p: Player, inventory: Inventory, toReturnInventory: Inventory): Boolean {
        var result = false
        for (item in inventory.contents.filterNotNull()) {
            val assigned = MapAssigner.getAssigned(item)
            if (assigned != null && assigned != p.uniqueId) {
                inventory.remove(item)
                addOrDrop(p, item, toReturnInventory)
                result = true
            }
        }

        return result
    }

    private fun addOrDrop(p: Player, item: ItemStack, toReturnInventory: Inventory) {
        val notAdded = toReturnInventory.addItem(item)
        notAdded.forEach { (_, u) ->
            p.location.world.dropItem(p.location, u)
        }
    }
}