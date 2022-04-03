package net.numalab.puzzle.puzzle

import net.numalab.puzzle.map.ImagedMap
import org.bukkit.World
import org.bukkit.inventory.ItemStack
import java.awt.image.BufferedImage

class ImagedPuzzle(val puzzle: Puzzle, val baseImageSet: Map<Pair<Int, Int>, BufferedImage>) {
    fun getImage(piece: Piece): BufferedImage? {
        return if (piece !in puzzle) null
        else {
            val pos = piece.x to piece.y
            baseImageSet[pos]
        }
    }

    fun toItemStacks(world: World): List<ItemStack> {
        return baseImageSet.mapNotNull { en ->
            val image = en.value
            val piece = puzzle[en.key.first, en.key.second]
            if (piece != null) {
                ImagedMap(image, piece).get(world)
            } else {
                null
            }
        }
    }
}