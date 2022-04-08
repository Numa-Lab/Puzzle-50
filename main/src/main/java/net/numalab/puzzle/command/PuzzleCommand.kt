package net.numalab.puzzle.command

import dev.kotx.flylib.command.Command
import net.kunmc.lab.configlib.ConfigCommandBuilder
import net.numalab.puzzle.PuzzleConfig
import net.numalab.puzzle.PuzzlePlugin

class PuzzleCommand(plugin: PuzzlePlugin, config: PuzzleConfig) : Command("pz") {
    init {
        description("Puzzle command")
        children(PuzzleLoadCommand(plugin), ConfigCommandBuilder(config).addConfig(config.defaultPuzzleSetting).build())
    }
}