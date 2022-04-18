package net.numalab.puzzle.command

import dev.kotx.flylib.command.Command
import dev.kotx.flylib.command.SuggestionAction
import net.numalab.puzzle.PuzzleConfig
import org.bukkit.Bukkit

class PuzzleTeamCommand(private val config: PuzzleConfig) : Command("team") {
    init {
        description("Team command of Puzzle Plugin")
        usage {
            selectionArgument("operation", "add", "remove")
            stringArgument(
                "team name",
                {
                    suggestAll(Bukkit.getScoreboardManager().mainScoreboard.teams.map { it.name })
                }
            )

            executes {
                val operation = this.typedArgs[0] as String
                val teamName = this.typedArgs[1] as String

                when (operation) {
                    "add" -> {
                        val team = Bukkit.getScoreboardManager().mainScoreboard.getTeam(teamName)
                        if (team != null) {
                            this@PuzzleTeamCommand.config.targetTeams.add(team)
                            success("チーム: $teamName を追加しました")
                        } else {
                            fail("チーム: $teamName は存在しません")
                        }
                    }
                    "remove" -> {
                        val team = Bukkit.getScoreboardManager().mainScoreboard.getTeam(teamName)
                        if (team != null) {
                            this@PuzzleTeamCommand.config.targetTeams.remove(team)
                            success("チーム: $teamName を削除しました")
                        } else {
                            fail("チーム: $teamName は存在しません")
                        }
                    }
                    else -> {
                        fail("不明な操作です")
                    }
                }
            }
        }
    }
}