package net.numalab.puzzle.listen

import net.numalab.puzzle.map.ImagedMapManager
import net.numalab.puzzle.solved.onSolved
import net.numalab.puzzle.puzzle.ImagedPuzzleManager
import net.numalab.puzzle.puzzle.Puzzle
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.ItemFrame
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin

fun isSolved(puzzle: Puzzle): Boolean {
    val imagedPuzzle = ImagedPuzzleManager.get(puzzle)
    return imagedPuzzle?.isSolved() ?: false
}

fun checkSolved(puzzle: Puzzle, location: Location, player: Player,plugin: Plugin) {
    val isSolved = isSolved(puzzle)
    if (isSolved) {
        onSolved(puzzle, location, player,plugin)
    }
}

fun checkSolved(itemFrame: ItemFrame, player: Player,plugin: Plugin) {
    val item = itemFrame.item
    if (item.type.isEmpty) return
    val map = ImagedMapManager.get(item) ?: return
    checkSolved(map.piece.puzzle, itemFrame.location, player,plugin)
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

fun Material.isMap(): Boolean {
    return this == Material.MAP || this == Material.FILLED_MAP
}