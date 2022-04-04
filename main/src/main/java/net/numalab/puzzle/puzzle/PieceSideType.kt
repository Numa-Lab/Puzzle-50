package net.numalab.puzzle.puzzle

import java.awt.Color

/**
 * ピースの辺の種類
 */
enum class PieceSideType(val color: Color?) {
    NONE(null),
    GRAY(Color(0xD3D4D9)),
    RED(Color(0xE15554)),
    GREEN(Color(0x6DA34D)),
    VIOLET(Color(0x7768AE)),
    CORN(Color(0xF2ED6F));

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