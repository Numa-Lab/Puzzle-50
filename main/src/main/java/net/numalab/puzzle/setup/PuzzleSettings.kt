package net.numalab.puzzle.setup

import org.bukkit.entity.Player
import java.net.URL


data class PuzzleSettings(
    val locationSelector: PuzzleLocationSelector,
    val setter: Player?,
    val size: PuzzleSizeSetting,
    val isShuffle: Boolean,
    val url: URL,
    val assignPieceMode: Boolean,
    val quitSettingMode: QuitSetting
)

fun PuzzleSettings.setUp() {
    PuzzleSetupper().setUp(this)
}