package net.numalab.puzzle.command

import dev.kotx.flylib.command.Command
import net.numalab.puzzle.PuzzlePlugin

class PuzzleGenerateCommand(val plugin: PuzzlePlugin) : Command("Generate") {
    init {
        usage {
            integerArgument("X(Zero-indexed)")
            integerArgument("Y(Zero-indexed)")

            executes {
                val x = this.typedArgs[0] as Int
                val y = this.typedArgs[1] as Int

                val p = this.player
                if (p != null) {
                    val b = this@PuzzleGenerateCommand.plugin.generateSelector.gen(p, x, y)
                    if (b) {
                        success("生成に成功しました")
                    } else {
                        fail("生成に失敗しました")
                        fail("/pz GenerateSelectorコマンドでパズルを選択してください")
                    }
                }
            }
        }
    }
}