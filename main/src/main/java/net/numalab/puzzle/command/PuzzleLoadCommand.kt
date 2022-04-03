package net.numalab.puzzle.command

import dev.kotx.flylib.command.Command
import dev.kotx.flylib.command.Permission
import dev.kotx.flylib.command.arguments.StringArgument
import net.numalab.puzzle.PuzzlePlugin
import net.numalab.puzzle.setup.PuzzleSetUpGUI
import java.net.MalformedURLException
import java.net.URL


class PuzzleLoadCommand(plugin: PuzzlePlugin) : Command("load") {
    init {
        description("Command to load a puzzle")
        permission(Permission.OP)
        usage {
            stringArgument("Puzzle Image URL", StringArgument.Type.PHRASE)

            executes {
                val p = this.player
                if (p == null) {
                    fail("プレイヤーから実行してください")
                } else {
                    val urlStr = this.typedArgs[0] as String
                    try {
                        val url = URL(urlStr)
                        val gui = PuzzleSetUpGUI(url, plugin.locationSelector)
                        gui.main(plugin).open(p)
                    } catch (e: MalformedURLException) {
                        fail("URLが正しくありません")
                    }
                }
            }
        }
    }
}