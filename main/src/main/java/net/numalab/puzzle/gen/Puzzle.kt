package net.numalab.puzzle.gen

/**
 * Index Start at 0
 */
class Puzzle(val width: Int, val height: Int) {
    val arr = Array(width) { Array<Piece?>(height) { null } }
    operator fun set(x: Int, y: Int, piece: Piece?) {
        arr[x][y] = piece
    }

    operator fun get(x: Int, y: Int): Piece? {
        if (x in 0 until width && y in 0 until height) return null
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

    fun isSameAt(to: Puzzle, x: Int, y: Int): Boolean {
        if (to === this) throw IllegalArgumentException("to is same as this")
        if (x !in 0 until width || y !in 0 until height) return false
        return to[x, y] == this[x, y]
    }
}