package net.numalab.puzzle.solved

import com.github.bun133.bukkitfly.component.plus
import com.github.bun133.bukkitfly.component.text
import com.github.bun133.bukkitfly.entity.firework.spawnFireWork
import com.github.bun133.bukkitfly.location.circle2D
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.numalab.puzzle.puzzle.Puzzle
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.FireworkEffect
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.meta.FireworkMeta
import kotlin.math.PI

fun onSolved(puzzle: Puzzle, location: Location, player: Player) {
    Bukkit.broadcast(text("パズルが完成しました！", NamedTextColor.GREEN))
    Bukkit.broadcast(player.displayName() + text("が最後の一ピースをはめました！", NamedTextColor.GREEN))

    val f: (FireworkMeta) -> Unit = { m: FireworkMeta ->
        m.power = 1
        m.addEffects(
            FireworkEffect.builder().trail(true).with(FireworkEffect.Type.BALL_LARGE).withColor(Color.YELLOW).build()
        )
    }

    location.circle2D(10.0, 2.0 * PI / 8).forEach {
        it.world.spawnFireWork(it, f)
    }
}