package net.numalab.puzzle.puzzle

/**
 * Index Start at 0
 */
class Puzzle(val width: Int, val height: Int) {
    val arr = Array(width) { Array<Piece?>(height) { null } }
    val attributes = mutableListOf<Any>()
    operator fun set(x: Int, y: Int, piece: Piece?) {
        arr[x][y] = piece
    }

    operator fun get(x: Int, y: Int): Piece? {
        if (x !in 0 until width || y !in 0 until height) return null
        return arr[x][y]
    }

    operator fun contains(piece: Piece): Boolean {
        for (x in 0 until width) {
            for (y in 0 until height) {
                if (arr[x][y] == piece) return true
            }
        }
        return false
    }
}