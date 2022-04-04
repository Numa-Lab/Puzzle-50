package net.numalab.puzzle.puzzle

class ImagedPuzzleManager {
    companion object {
        val puzzle = mutableMapOf<Puzzle, ImagedPuzzle>()

        fun register(imagedPuzzle: ImagedPuzzle) {
            this.puzzle[imagedPuzzle.puzzle] = imagedPuzzle
        }

        fun get(puzzle: Puzzle): ImagedPuzzle? {
            return this.puzzle[puzzle]
        }

        fun get(piece: Piece): ImagedPuzzle? {
            return this.puzzle[piece.puzzle]
        }
    }
}