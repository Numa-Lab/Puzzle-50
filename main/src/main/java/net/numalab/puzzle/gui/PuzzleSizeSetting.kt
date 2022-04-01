package net.numalab.puzzle.gui

enum class PuzzleSizeSetting(val value: Double) {
    // 100%
    `100%`(1.0),

    // 75%
    `75%`(0.75),

    // 50%
    `50%`(0.5),

    // 25%
    `25%`(0.25),

    // 10%
    `10%`(0.1),

    // 150%
    `150%`(1.5),

    // 200%
    `200%`(2.0),

    // 300%
    `300%`(3.0),

    // 400%
    `400%`(4.0);

    fun apply(value: Double): Double {
        return this.value * value
    }
}