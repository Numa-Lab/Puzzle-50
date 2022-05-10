package net.numalab.puzzle.hint

import com.github.bun133.bukkitfly.stack.addOrDrop
import net.numalab.puzzle.map.ImagedMapManager
import net.numalab.puzzle.puzzle.Puzzle
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

/**
 * 最悪時のピースを出すクラス
 */
class Generator(val plugin: JavaPlugin) {
    fun genToPlayer(puzzle: Puzzle, x: Int, y: Int, player: Player): Boolean {
        val piece = puzzle[x, y]
        return if (piece == null) {
            false
        } else {
            val imagedMap = ImagedMapManager.get(piece)
            if (imagedMap == null) {
                false
            } else {
                val stack = imagedMap.get(player.world)
                player.inventory.addOrDrop(stack)
                true
            }
        }
    }
}