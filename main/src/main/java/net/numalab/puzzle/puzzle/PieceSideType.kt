package net.numalab.puzzle.puzzle

import java.awt.Color

/**
 * ピースの辺の種類
 */
enum class PieceSideType(val color: Color?) {
    // まっ平
    NONE(null),

    // へこんでいる辺
    BLUE(Color.BLUE),

    // 出っ張っている辺
    RED(Color.RED);

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