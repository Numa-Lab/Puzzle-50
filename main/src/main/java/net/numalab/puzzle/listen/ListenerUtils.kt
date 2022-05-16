package net.numalab.puzzle.listen

import com.github.bun133.bukkitfly.component.text
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor
import net.numalab.puzzle.PuzzleConfig
import net.numalab.puzzle.map.ImagedMap
import net.numalab.puzzle.map.ImagedMapManager
import net.numalab.puzzle.solved.onSolved
import net.numalab.puzzle.puzzle.ImagedPuzzleManager
import net.numalab.puzzle.puzzle.Puzzle
import net.numalab.puzzle.team.TeamSessionData
import org.bukkit.ChatColor
import org.bukkit.GameMode
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

fun Puzzle.isCelebrated(): Boolean {
    return this.attributes.find { it is Boolean } as Boolean? ?: false
}

fun Puzzle.setCelebrated(b: Boolean) {
    this.attributes.removeAll { it is Boolean }
    this.attributes.add(b)
}

fun checkSolved(puzzle: Puzzle, location: Location, player: Player, plugin: Plugin) {
    val isSolved = isSolved(puzzle)
    if (isSolved && !puzzle.isCelebrated()) {
        puzzle.setCelebrated(true)
        onSolved(puzzle, location, player, plugin)
    }
}

fun checkSolved(itemFrame: ItemFrame, player: Player, plugin: Plugin) {
    val item = itemFrame.item
    if (item.type.isEmpty) return
    val map = ImagedMapManager.get(item) ?: return
    checkSolved(map.piece.puzzle, itemFrame.location, player, plugin)
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

fun ImagedMap.teamSession(): TeamSessionData? {
    return this.piece.puzzle.attributes.filterIsInstance(TeamSessionData::class.java).firstOrNull()
}

fun ImagedMap.checkInteractive(player: Player, conf: PuzzleConfig): Boolean {
    val teamSession = this.teamSession()
    return if (teamSession == null) {
        player.gameMode == GameMode.CREATIVE || conf.players().contains(player)
    } else {
        player.gameMode == GameMode.CREATIVE || teamSession.team.second.map { it.uniqueId }.contains(player.uniqueId)
    }
}

fun ImagedMap.getNonInteractiveMessage(): TextComponent {
    val session = this.teamSession()
    if (session == null) {
        return text("このピースをあなたはいじることができません", NamedTextColor.RED)
    } else {
        return text("このピースは${session.team.first}の物なので、あなたはいじることができません", NamedTextColor.RED)
    }
}