package net.numalab.puzzle.gen

import net.numalab.puzzle.puzzle.Piece
import net.numalab.puzzle.puzzle.PieceSideType
import net.numalab.puzzle.puzzle.Puzzle

interface PuzzleGenerator {
    fun generate(setting: PuzzleGenerateSetting): Puzzle
}

data class PuzzleGenerateSetting(
    val width: Int,
    val height: Int,
    val isFlat: Boolean
)

class DefaultPuzzleGenerator : PuzzleGenerator {
    override fun generate(setting: PuzzleGenerateSetting): Puzzle {
        val puzzle = Puzzle(setting.width, setting.height)
        for (x in 0 until setting.width) {
            for (y in 0 until setting.height) {
                puzzle[x, y] = Piece(PieceSideType.NONE, PieceSideType.NONE, PieceSideType.NONE, PieceSideType.NONE,puzzle,x,y)
            }
        }
        if (!setting.isFlat) {
            for (x in 0 until setting.width) {
                for (y in 0 until setting.height) {
                    if (x != 0) {
                        puzzle[x, y]!!.left = puzzle[x - 1, y]!!.right
                    }

                    if (y != 0) {
                        puzzle[x, y]!!.top = puzzle[x, y - 1]!!.bottom
                    }

                    if (x != setting.width - 1) {
                        puzzle[x, y]!!.right = PieceSideType.random(false)
                    }

                    if (y != setting.height - 1) {
                        puzzle[x, y]!!.bottom = PieceSideType.random(false)
                    }
                }
            }
        }
        return puzzle
    }
}