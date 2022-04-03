package net.numalab.puzzle.puzzle

class Piece(
    var top: PieceSideType,
    var bottom: PieceSideType,
    var right: PieceSideType,
    var left: PieceSideType,
    val x: Int,
    val y: Int
) {
    /**
     * 右回しに90度回されたときのピース
     */
    fun rotate() {
        val btop = top
        val bbottom = bottom
        val bleft = left
        val bright = right

        top = bleft
        bottom = bright
        right = btop
        left = bbottom
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