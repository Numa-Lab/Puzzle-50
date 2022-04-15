package net.numalab.puzzle.hint

import com.github.bun133.guifly.value.Value
import net.numalab.puzzle.PuzzlePlugin
import net.numalab.puzzle.map.assign.MapAssigner
import net.numalab.puzzle.puzzle.Puzzle
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.*

class Emphasize(var puzzle: Value<Puzzle?>, val plugin: PuzzlePlugin) {
    init {
        puzzle.listen {
            updateAll()
        }
    }

    private val map = mutableMapOf<UUID, Value<Boolean>>()
    val isEnabled = Value(false).also {
        it.listen {
            updateAll()
        }
    }

    fun updateAll() {
        plugin.config.players().forEach {
            updatePlayer(it)
        }
    }

    fun updatePlayer(pl: Player) {
        val p = puzzle.value
        if (p != null) {
            set(pl, !MapAssigner.isCompleted(pl, p))
        } else {
            set(pl, false)
        }
    }

    private fun set(player: Player, isEmphasized: Boolean) {
        getOrGenerate(player).value = isEmphasized
    }

    private fun getOrGenerate(player: Player): Value<Boolean> {
        return map[player.uniqueId] ?: generate(player)
    }

    private fun generate(p: Player): Value<Boolean> {
        val v = Value(false)
        v.listen {
            update(this, p.uniqueId)
        }
        map[p.uniqueId] = v
        return v
    }

    private fun update(value: Value<Boolean>, uniqueId: UUID) {
        if (!isEnabled.value) return
        val p = Bukkit.getPlayer(uniqueId)
        if (p != null) {
            if (value.value) {
                p.addPotionEffect(PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 1))
            } else {
                p.removePotionEffect(PotionEffectType.GLOWING)
            }
        }
    }
}