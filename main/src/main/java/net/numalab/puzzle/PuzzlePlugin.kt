package net.numalab.puzzle

import dev.kotx.flylib.flyLib
import net.numalab.puzzle.command.PuzzleCommand
import net.numalab.puzzle.listen.PickUpListener
import net.numalab.puzzle.listen.PlaceListener
import net.numalab.puzzle.listen.RemoveListener
import net.numalab.puzzle.listen.RotateListener
import net.numalab.puzzle.map.ImagedMap
import net.numalab.puzzle.map.ImagedMapManager
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
        PickUpListener(this)
        RotateListener(this)
        RemoveListener(this)
        PlaceListener(this)
    }

    init {
        flyLib {
            command(PuzzleCommand(this@PuzzlePlugin, config))
        }
    }

    override fun onDisable() {
        config.saveConfigIfPresent()
    }

    fun reset() {
        locationSelector.reset()
        ImagedMapManager.reset()
    }
}