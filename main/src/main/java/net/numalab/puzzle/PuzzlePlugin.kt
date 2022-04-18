package net.numalab.puzzle

import com.github.bun133.guifly.value.Value
import dev.kotx.flylib.flyLib
import net.numalab.puzzle.command.PuzzleCommand
import net.numalab.puzzle.hint.Emphasize
import net.numalab.puzzle.hint.EmphasizeSelector
import net.numalab.puzzle.listen.*
import net.numalab.puzzle.map.ImagedMap
import net.numalab.puzzle.map.ImagedMapManager
import net.numalab.puzzle.map.assign.MapAssigner
import net.numalab.puzzle.puzzle.ImagedPuzzleManager
import net.numalab.puzzle.puzzle.Puzzle
import net.numalab.puzzle.setup.PuzzleLocationSelector
import net.numalab.puzzle.solved.InteractPrevent
import org.bukkit.plugin.java.JavaPlugin

class PuzzlePlugin : JavaPlugin() {
    val config = PuzzleConfig(this).also {
        it.saveConfigIfAbsent()
        it.loadConfig()
    }

    lateinit var locationSelector: PuzzleLocationSelector
    lateinit var emphasizeSelector: EmphasizeSelector
    val emphasize = Emphasize(Value(null), this)

    override fun onEnable() {
        locationSelector = PuzzleLocationSelector(this)
        emphasizeSelector = EmphasizeSelector(this)
        PickUpListener(this)
        RotateListener(this)
        RemoveListener(this)
        PlaceListener(this)
        QuitListener(this)
        DropListener(this)
        InteractPrevent(this)
    }

    init {
        flyLib {
            command(PuzzleCommand(this@PuzzlePlugin, config))
        }
    }

    override fun onDisable() {
        reset()
        config.saveConfigIfPresent()
    }

    fun reset() {
        locationSelector.reset()
        emphasizeSelector.reset()
        ImagedMapManager.reset()
        MapAssigner.reset()
        emphasize.puzzle.value = null
        ImagedPuzzleManager.reset()
    }
}