package net.numalab.puzzle.gen

/**
 * ピースの辺の種類
 */
enum class PieceSideType {
    // まっ平
    NONE,

    // へこんでいる辺
    DINT,

    // 出っ張っている辺
    CONVEX;

    fun getOpposite(): PieceSideType {
        return when (this) {
            NONE -> NONE
            DINT -> CONVEX
            CONVEX -> DINT
        }
    }

    companion object {
        fun random(containNone: Boolean): PieceSideType {
            return if (containNone) {
                values().random()
            } else {
                (values().toList() - NONE).random()
            }
        }
    }
}