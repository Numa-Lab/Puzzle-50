package net.numalab.puzzle

import dev.kotx.flylib.flyLib
import net.numalab.puzzle.command.PuzzleCommand
import org.bukkit.plugin.java.JavaPlugin

class PuzzlePlugin : JavaPlugin() {
    val config = PuzzleConfig(this).also {
        it.saveConfigIfAbsent()
        it.loadConfig()
    }

    init {
        flyLib {
            command(PuzzleCommand())
        }
    }

    override fun onDisable() {
        config.saveConfigIfPresent()
    }
}