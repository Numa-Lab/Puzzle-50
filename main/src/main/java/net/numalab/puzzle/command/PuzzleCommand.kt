package net.numalab.puzzle.command

import dev.kotx.flylib.command.Command
import net.numalab.puzzle.PuzzlePlugin

class PuzzleCommand(plugin: PuzzlePlugin) : Command("pz") {
    init {
        description("Puzzle command")
        children(PuzzleLoadCommand(plugin))
    }
}