package net.numalab.puzzle

import dev.kotx.flylib.flyLib
import net.numalab.puzzle.command.PuzzleCommand
import net.numalab.puzzle.setup.PuzzleLocationSelector
import org.bukkit.plugin.java.JavaPlugin

class PuzzlePlugin : JavaPlugin() {
    val config = PuzzleConfig(this).also {
        it.saveConfigIfAbsent()
        it.loadConfig()
    }

    lateinit var locationSelector: PuzzleLocationSelector
    override fun onEnable() {
        locationSelector = PuzzleLocationSelector(this)
    }

    init {
        flyLib {
            command(PuzzleCommand(this@PuzzlePlugin))
        }
    }

    override fun onDisable() {
        config.saveConfigIfPresent()
    }

    fun reset() {
        locationSelector.reset()
    }
}