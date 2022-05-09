package net.numalab.puzzle

import net.kunmc.lab.configlib.BaseConfig
import net.kunmc.lab.configlib.value.BooleanValue
import net.kunmc.lab.configlib.value.EnumValue
import net.kunmc.lab.configlib.value.collection.TeamSetValue
import net.numalab.puzzle.setup.PuzzleLocationSelector
import net.numalab.puzzle.setup.PuzzleSettings
import net.numalab.puzzle.setup.PuzzleSizeSetting
import net.numalab.puzzle.setup.QuitSetting
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.net.URL

class PuzzleConfig(plugin: Plugin) : BaseConfig(plugin) {
    val targetGameMode = EnumValue<GameMode>(GameMode.SURVIVAL)

    fun players() = Bukkit.getOnlinePlayers().filter { it.gameMode == targetGameMode.value() }.filterNotNull()

    val targetTeams = TeamSetValue()

    val defaultPuzzleSetting = DefaultPuzzleSetting(plugin, this)
    val lockPuzzleAfterSolved = BooleanValue(true)

    fun teams(): List<Pair<String, List<Player>>> {
        return players()
            .groupBy { Bukkit.getScoreboardManager().mainScoreboard.getEntryTeam(it.name) }
            .filterKeys { it != null }
            .filterKeys { targetTeams.value().contains(it) }
            .mapKeys { it.key!!.name }
            .toList()
    }

    /**
     * テスト用
     */
    val logOutAssertionResult = BooleanValue(true)
}

class DefaultPuzzleSetting(plugin: Plugin, val conf: PuzzleConfig) : BaseConfig(plugin) {
    fun toSettings(locationSelector: PuzzleLocationSelector, url: URL): PuzzleSettings {
        return if (isTeamMode.value()) {
            PuzzleSettings(
                locationSelector,
                null,
                size.value(),
                isShuffle.value(),
                url,
                isAssign.value(),
                quitSettingMode.value(),
                conf.teams(),
                toSetUpFrame.value()
            )
        } else {
            PuzzleSettings(
                locationSelector,
                null,
                size.value(),
                isShuffle.value(),
                url,
                isAssign.value(),
                quitSettingMode.value(),
                listOf("DefaultTeam" to conf.players()),
                toSetUpFrame.value()
            )
        }
    }

    fun applySettings(settings: PuzzleSettings) {
        this.size.value(settings.size)
        this.isShuffle.value(settings.isShuffle)
        this.isAssign.value(settings.assignPieceMode)
        this.quitSettingMode.value(settings.quitSettingMode)
        this.isTeamMode.value(settings.targetPlayers.size != 1)
        this.toSetUpFrame.value(settings.toSetUpFrame)
    }

    val size = EnumValue<PuzzleSizeSetting>(PuzzleSizeSetting.`100%`)

    val isShuffle = BooleanValue(false)

    val isAssign = BooleanValue(false)

    val quitSettingMode = EnumValue<QuitSetting>(QuitSetting.None)

    val isTeamMode = BooleanValue(false)

    val toSetUpFrame = BooleanValue(false)
}