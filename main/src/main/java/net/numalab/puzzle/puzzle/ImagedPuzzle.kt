package net.numalab.puzzle.puzzle

import net.numalab.puzzle.map.ImagedMap
import org.bukkit.World
import org.bukkit.inventory.ItemStack
import java.awt.image.BufferedImage

class ImagedPuzzle(val puzzle: Puzzle, val baseImageSet: List<ImagedMap>) {
    init {
        ImagedPuzzleManager.register(this)
    }

    fun toItemStacks(world: World): List<ItemStack> {
        return baseImageSet.map {
            it.get(world)
        }
    }

    fun isSolved() = baseImageSet.all { it.isSolved } && baseImageSet.isNotEmpty()

    // ピース・画像を回転した状態で保存する
    fun shuffle() {
        baseImageSet.forEach { it.shuffle() }
    }
}