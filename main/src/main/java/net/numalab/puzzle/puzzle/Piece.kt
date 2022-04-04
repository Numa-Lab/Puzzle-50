package net.numalab.puzzle.puzzle

import org.bukkit.block.BlockFace
import java.util.UUID

class Piece(
    var top: PieceSideType,
    var bottom: PieceSideType,
    var right: PieceSideType,
    var left: PieceSideType,
    val x: Int,
    val y: Int
) {
    val uuid = UUID.randomUUID()


    fun get(direction: BlockFace): PieceSideType {
        return when (direction) {
            BlockFace.NORTH -> top
            BlockFace.SOUTH -> bottom
            BlockFace.EAST -> right
            BlockFace.WEST -> left
            else -> throw IllegalArgumentException("direction is not valid")
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return if (other is Piece) {
            top == other.top && bottom == other.bottom && right == other.right && left == other.left && x == other.x && y == other.y
        } else {
            false
        }
    }
}