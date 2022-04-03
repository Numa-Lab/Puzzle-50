package net.numalab.puzzle.puzzle

import net.numalab.puzzle.map.ImagedMap
import org.bukkit.World
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import java.awt.image.BufferedImage

class ImagedPuzzle(val puzzle: Puzzle, val imageSet: Map<Pair<Int, Int>, BufferedImage>) {
    fun getImage(piece: Piece): BufferedImage? {
        return if (piece !in puzzle) null
        else {
            val pos = piece.x to piece.y
            imageSet[pos]
        }
    }

    fun toItemStacks(world:World): List<ItemStack> {
        return imageSet.map { en ->
            val image = en.value
            ImagedMap(image).get(world)
        }
    }
}