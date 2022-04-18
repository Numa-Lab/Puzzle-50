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
import net.numalab.puzzle.solved.solvedResult
import org.bukkit.plugin.java.JavaPlugin

class PuzzlePlugin : JavaPlugin() {
    lateinit var config: PuzzleConfig

    lateinit var locationSelector: PuzzleLocationSelector
    lateinit var emphasizeSelector: EmphasizeSelector
    val emphasize = Emphasize(Value(null), this)

    override fun onEnable() {
        config = PuzzleConfig(this).also {
            it.saveConfigIfAbsent()
            it.loadConfig()
        }

        flyLib {
            command(PuzzleCommand(this@PuzzlePlugin, config))
        }

        locationSelector = PuzzleLocationSelector(this)
        emphasizeSelector = EmphasizeSelector(this)
        PickUpListener(this)
        PlaceListener(this)
        QuitListener(this)
        DropListener(this)
        InteractPrevent(
            this, RotateListener(this), RemoveListener(this)
        )
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
        solvedResult.clear()
    }
}