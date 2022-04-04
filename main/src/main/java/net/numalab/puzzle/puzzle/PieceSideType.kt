package net.numalab.puzzle.puzzle

import java.awt.Color

/**
 * ピースの辺の種類
 */
enum class PieceSideType(val color: Color?) {
    NONE(null),
    BLUE(Color.BLUE),
    GREEN(Color.GREEN),
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