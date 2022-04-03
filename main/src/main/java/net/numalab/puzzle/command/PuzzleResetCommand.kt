package net.numalab.puzzle.command

import dev.kotx.flylib.command.Command
import net.numalab.puzzle.PuzzlePlugin


class PuzzleResetCommand(plugin: PuzzlePlugin) : Command("reset") {
    init {
        description("強制的にすべてをリセットします")
        usage {
            selectionArgument("really?", "really")
            executes {
                val s = typedArgs[0] as String
                if (s == "really") {
                    sender.sendMessage("リセットします")
                    plugin.reset()
                } else {
                    sender.sendMessage("リセットしません")
                }
            }
        }
    }
}