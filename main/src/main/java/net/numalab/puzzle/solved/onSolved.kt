package net.numalab.puzzle.solved

import com.github.bun133.bukkitfly.advancement.ToastData
import com.github.bun133.bukkitfly.advancement.toast
import com.github.bun133.bukkitfly.component.plus
import com.github.bun133.bukkitfly.component.text
import com.github.bun133.bukkitfly.entity.firework.spawnFireWork
import com.github.bun133.bukkitfly.location.circle2D
import net.kyori.adventure.text.format.NamedTextColor
import net.numalab.puzzle.puzzle.Puzzle
import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.inventory.meta.FireworkMeta
import org.bukkit.plugin.Plugin
import kotlin.math.PI

private val clearAdv: () -> ToastData = {
    ToastData(
        Material.STONE,
        "パズルクリア！",
        ToastData.FrameType.Challenge
    )
}

private val clear100Adv: () -> ToastData = {ToastData(
    Material.DIAMOND,
    "そこそこのパズルクリア！",
    ToastData.FrameType.Challenge
)}

private val clear1000Adv: () -> ToastData ={ ToastData(
    Material.EMERALD,
    "大きなパズルクリア！",
    ToastData.FrameType.Challenge
)}

fun onSolved(puzzle: Puzzle, location: Location, player: Player, plugin: Plugin) {
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

    val is100 = puzzle.width * puzzle.height >= 100
    val is1000 = puzzle.width * puzzle.height >= 1000

    println("is100: $is100, is1000: $is1000")

    Bukkit.getOnlinePlayers().forEach {
        it.toast(clearAdv(), plugin)
        if (is100) it.toast(clear100Adv(), plugin)
        if (is1000) it.toast(clear1000Adv(), plugin)
    }
}