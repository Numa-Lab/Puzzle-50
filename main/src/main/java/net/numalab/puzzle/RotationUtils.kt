package net.numalab.puzzle

import org.bukkit.Rotation
import org.bukkit.block.BlockFace

class RotationUtils {
    companion object {
        val rotationMap = Rotation.values().associateWith { it.ordinal }

        val blockFaceMap = mapOf(
            Rotation.NONE to BlockFace.NORTH,
            Rotation.CLOCKWISE_45 to BlockFace.EAST,
            Rotation.CLOCKWISE to BlockFace.SOUTH,
            Rotation.CLOCKWISE_135 to BlockFace.WEST,
            Rotation.FLIPPED to BlockFace.NORTH,
            Rotation.FLIPPED_45 to BlockFace.WEST,
            Rotation.COUNTER_CLOCKWISE to BlockFace.SOUTH,
            Rotation.COUNTER_CLOCKWISE_45 to BlockFace.EAST
        )

        val blockFaces = listOf(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST)

        fun addRotation(one: Rotation, two: Rotation): Rotation {
            val oneI = rotationMap[one]
            val twoI = rotationMap[two]

            if (oneI == null || twoI == null) {
                throw IllegalArgumentException("Rotation is not supported")
            }

            val result = (oneI + twoI) % 4
            return rotationMap.entries.first { it.value == result }.key
        }

        fun rotationToFace(rotation: Rotation): BlockFace {
            return blockFaceMap[rotation] ?: throw IllegalArgumentException("$rotation is not supported")
        }

        fun add45(rotation: Rotation): Rotation {
            val i = Rotation.values().indexOf(rotation)
            return Rotation.values()[(i + 1) % 8]
        }

        fun decrementRotation(face: BlockFace, rotation: Rotation): BlockFace {
            val times = rotationMap[rotation]!!
            val index = blockFaces.indexOf(face)
            if (index == -1) {
                throw IllegalArgumentException("$face is not supported")
            }

            val result = (index - times + 8) % 4

            return blockFaces[result]
        }
    }
}