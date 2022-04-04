package net.numalab.puzzle.listen

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.numalab.puzzle.map.ImagedMapManager
import net.numalab.puzzle.puzzle.ImagedPuzzleManager
import net.numalab.puzzle.puzzle.Puzzle
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.ItemFrame
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

fun isSolved(puzzle: Puzzle): Boolean {
    val imagedPuzzle = ImagedPuzzleManager.get(puzzle)
    return imagedPuzzle?.isSolved() ?: false
}

fun checkSolved(puzzle: Puzzle, location: Location, player: Player) {
    val isSolved = isSolved(puzzle)
    if (isSolved) {
        Bukkit.broadcast(Component.text("パズルが完成しました").color(NamedTextColor.GREEN))
        Bukkit.broadcast(player.displayName().color(NamedTextColor.GREEN).append(Component.text("がパズルを完成しました!")))
    }
}

fun checkSolved(itemFrame: ItemFrame, player: Player) {
    val item = itemFrame.item
    if (item.type.isEmpty) return
    val map = ImagedMapManager.get(item) ?: return
    checkSolved(map.piece.puzzle, itemFrame.location, player)
}

fun update(itemFrame: ItemFrame) {
    val item = itemFrame.item
    item(item, itemFrame)
}

fun item(item: ItemStack, frame: ItemFrame) {
    if (item.type != Material.MAP && item.type != Material.FILLED_MAP) return
    val map = ImagedMapManager.get(item)
    if (map != null) {
        val stacks = ImagedMapManager.getAllStack(map)
        stacks.forEach {
            map.updateStack(it, frame, frame.rotation)
        }
    }
}