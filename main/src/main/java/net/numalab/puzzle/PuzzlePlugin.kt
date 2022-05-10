package net.numalab.puzzle

import com.github.bun133.guifly.value.Value
import com.github.bun133.testfly.report
import dev.kotx.flylib.flyLib
import net.numalab.puzzle.command.PuzzleCommand
import net.numalab.puzzle.hint.Emphasize
import net.numalab.puzzle.hint.EmphasizeSelector
import net.numalab.puzzle.hint.Generator
import net.numalab.puzzle.hint.GeneratorSelector
import net.numalab.puzzle.listen.*
import net.numalab.puzzle.map.ImagedMapManager
import net.numalab.puzzle.map.assign.MapAssigner
import net.numalab.puzzle.puzzle.ImagedPuzzleManager
import net.numalab.puzzle.setup.PuzzleLocationSelector
import net.numalab.puzzle.solved.InteractPrevent
import net.numalab.puzzle.solved.solvedResult
import net.numalab.puzzle.test.PuzzleAssertion
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class PuzzlePlugin : JavaPlugin() {
    lateinit var config: PuzzleConfig

    lateinit var locationSelector: PuzzleLocationSelector
    lateinit var emphasizeSelector: EmphasizeSelector
    val emphasize = Emphasize(Value(null), this)
    lateinit var generateSelector: GeneratorSelector
    val generator = Generator(this)

    var assertion = PuzzleAssertion(this)

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
        generateSelector = GeneratorSelector(this)
        PickUpListener(this)
        PlaceListener(this)
        QuitListener(this)
        DropListener(this)
        InteractPrevent(
            this, RotateListener(this), RemoveListener(this)
        )
        InventoryTransferListener(this)
    }

    override fun onDisable() {
        if (config.logOutAssertionResult.value()) {
            val report = this.report()
            report.second.forEach {
                println(it)
            }

            println("")
            println("")
            println("Assertion Result:${report.first}")
        }
        reset()
        config.saveConfigIfPresent()
    }

    fun reset() {
        locationSelector.reset()
        emphasizeSelector.reset()
        generateSelector.reset()
        ImagedMapManager.reset()
        MapAssigner.reset()
        emphasize.puzzle.value = null
        ImagedPuzzleManager.reset()
        solvedResult.clear()
        assertion = PuzzleAssertion(this)
        clearAllEffect()
    }

    private fun clearAllEffect() {
        Bukkit.getOnlinePlayers().forEach { p ->
            p.activePotionEffects.toList().forEach {
                p.removePotionEffect(it.type)
            }
        }
    }
}