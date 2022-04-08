package net.numalab.puzzle

import net.kunmc.lab.configlib.BaseConfig
import net.kunmc.lab.configlib.value.BooleanValue
import net.kunmc.lab.configlib.value.EnumValue
import net.numalab.puzzle.setup.PuzzleLocationSelector
import net.numalab.puzzle.setup.PuzzleSettings
import net.numalab.puzzle.setup.PuzzleSizeSetting
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.net.URL

class PuzzleConfig(plugin: Plugin) : BaseConfig(plugin) {
    val defaultPuzzleSetting = DefaultPuzzleSetting(plugin)
}

class DefaultPuzzleSetting(plugin: Plugin) : BaseConfig(plugin) {
    fun toSettings(locationSelector: PuzzleLocationSelector, url: URL): PuzzleSettings {
        return PuzzleSettings(
            locationSelector,
            null,
            size.value(),
            isShuffle.value(),
            url
        )
    }

    fun applySettings(settings: PuzzleSettings) {
        this.size.value(settings.size)
        this.isShuffle.value(settings.isShuffle)
    }

    val size = EnumValue<PuzzleSizeSetting>(PuzzleSizeSetting.`100%`)

    val isShuffle = BooleanValue(false)
}