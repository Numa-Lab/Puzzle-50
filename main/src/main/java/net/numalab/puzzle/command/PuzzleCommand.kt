package net.numalab.puzzle.command

import dev.kotx.flylib.command.Command
import net.kunmc.lab.configlib.ConfigCommandBuilder
import net.numalab.puzzle.PuzzleConfig
import net.numalab.puzzle.PuzzlePlugin

class PuzzleCommand(val plugin: PuzzlePlugin, config: PuzzleConfig) : Command("pz") {
    init {
        description("Puzzle command")
        children(
            PuzzleLoadCommand(plugin),
            PuzzleResetCommand(plugin),
            PuzzleTeamCommand(config),
            ConfigCommandBuilder(config).addConfig(config.defaultPuzzleSetting).build()
        )

        usage {
            selectionArgument("hint", "hint")
            executes {
                when (typedArgs[0] as String) {
                    "hint" -> {
                        if (player != null) {
                            success("ヒントを表示したいパズルのピースを回転させてください")
                            this@PuzzleCommand.plugin.emphasize.isEnabled.value = false
                            this@PuzzleCommand.plugin.emphasizeSelector.playerQueue.add(player!!.uniqueId)
                        } else {
                            fail("このコマンドはコンソールから実行できません")
                        }
                    }
                }
            }
        }
    }
}