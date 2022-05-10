package net.numalab.puzzle.command

import dev.kotx.flylib.command.Command
import net.kunmc.lab.configlib.ConfigCommandBuilder
import net.kyori.adventure.text.format.NamedTextColor
import net.numalab.puzzle.PuzzleConfig
import net.numalab.puzzle.PuzzlePlugin

class PuzzleCommand(val plugin: PuzzlePlugin, config: PuzzleConfig) : Command("pz") {
    init {
        description("Puzzle command")
        children(
            PuzzleLoadCommand(plugin),
            PuzzleResetCommand(plugin),
            PuzzleTeamCommand(config),
            ConfigCommandBuilder(config).addConfig(config.defaultPuzzleSetting).build(),
            PuzzleGenerateCommand(plugin)
        )

        usage {
            selectionArgument("hint", "Emphasize", "GenerateSelector")
            executes {
                when (typedArgs[0] as String) {
                    "Emphasize" -> {
                        if (player != null) {
                            success("ヒントを表示したいパズルのピースを回転させてください")
                            this@PuzzleCommand.plugin.emphasize.isEnabled.value = false
                            this@PuzzleCommand.plugin.emphasizeSelector.playerQueue.add(player!!.uniqueId)
                        } else {
                            fail("このコマンドはコンソールから実行できません")
                        }
                    }

                    "GenerateSelector" -> {
                        if (player != null) {
                            if (this@PuzzleCommand.plugin.generateSelector.isIn(player!!.uniqueId)) {
                                success("ピース生成モードを終了しました")
                                this@PuzzleCommand.plugin.generateSelector.remove(player!!.uniqueId)
                            } else {
                                success("ピース生成モードに変更しました")
                                success("ピースを回転させてください")
                                this@PuzzleCommand.plugin.generateSelector.playerQueue.add(player!!.uniqueId)
                            }
                        }
                    }
                }
            }
        }
    }
}