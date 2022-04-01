package net.numalab.puzzle.command

import dev.kotx.flylib.command.Command

class PuzzleCommand : Command("pz") {
    init {
        description("Puzzle command")
        children(PuzzleLoadCommand())
    }
}