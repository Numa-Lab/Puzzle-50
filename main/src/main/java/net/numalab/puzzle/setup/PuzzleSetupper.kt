package net.numalab.puzzle.setup

import com.github.bun133.bukkitfly.component.text
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.numalab.puzzle.gen.DefaultPuzzleGenerator
import net.numalab.puzzle.gen.PuzzleGenerateSetting
import net.numalab.puzzle.geo.FrameFiller
import net.numalab.puzzle.hint.Emphasize
import net.numalab.puzzle.img.ImageLoader
import net.numalab.puzzle.img.ImageResizer
import net.numalab.puzzle.img.ImageSplitter
import net.numalab.puzzle.map.ImagedMap
import net.numalab.puzzle.map.assign.MapAssigner
import net.numalab.puzzle.puzzle.ImagedPuzzle
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.awt.image.BufferedImage
import kotlin.math.ceil

class PuzzleSetupper {
    fun setUp(settings: PuzzleSettings) {
        val player = settings.setter
        if (player == null) {
            println("[ERROR] player is null")
            return
        }
        val sizeValue = settings.size
        val url = settings.url
        val isShuffle = settings.isShuffle

        println("size: $sizeValue")
        println("url: $url")
        player.sendMessage("" + ChatColor.GREEN + "画像読み込み中...")
        val img = ImageLoader().loadImage(url)
        if (img == null) {
            player.sendMessage("" + ChatColor.RED + "画像の読み込みに失敗しました")
            return
        }
        val resizedImage: BufferedImage?
        try {
            resizedImage =
                ImageResizer().resize(
                    img, sizeValue.apply(img.width.toDouble()).toInt(),
                    sizeValue.apply(img.height.toDouble()).toInt()
                )
        } catch (e: java.lang.IllegalArgumentException) {
            player.sendMessage("" + ChatColor.RED + "画像の読み込みに失敗しました")
            return
        }

        player.sendMessage("" + ChatColor.GREEN + "画像読み込み完了")

        val split = ImageSplitter().split(resizedImage, 128, 128)

        val xColumn = ceil(resizedImage.width / 128.0).toInt()
        val yRow = ceil(resizedImage.height / 128.0).toInt()

        player.sendMessage("" + ChatColor.GREEN + "パズル生成中...")
        val mock =
            DefaultPuzzleGenerator().generate(PuzzleGenerateSetting(xColumn, yRow, false))
        val imaged = split.map {
            ImagedMap(it.value, mock[it.key.first, it.key.second]!!)
        }

        val imagedPuzzle = ImagedPuzzle(mock, imaged)

        val stacks = imagedPuzzle.toItemStacks(player.world)

        val finalStacks = if (isShuffle) {
            stacks.shuffled()
        } else {
            stacks
        }

        player.sendMessage(Component.empty())

        player.sendMessage("" + ChatColor.GREEN + "パズルを作成しました")
        player.sendMessage("" + ChatColor.GREEN + "縦:${yRow} 横:${xColumn}の計${xColumn * yRow}ピースです")

        player.sendMessage(Component.empty())

        if (settings.assignPieceMode) {
            player.sendMessage("" + ChatColor.GREEN + "ピース割り当て中...")
            if (!assignToPlayers(finalStacks, settings.targetPlayers)) {
                return
            }
            when (settings.quitSettingMode) {
                QuitSetting.AssignToAll -> {
                    player.sendMessage(text("退出時に再割り当てが行われます", NamedTextColor.DARK_RED))
                }
                QuitSetting.None -> {
                    player.sendMessage(text("退出時に再割り当ては行われません", NamedTextColor.DARK_RED))
                }
            }
            imagedPuzzle.puzzle.attributes.add(settings.quitSettingMode)

            player.sendMessage(Component.empty())
        }

        player.sendMessage("" + ChatColor.GREEN + "ブロックをクリックして開始位置を指定してください")

        settings.locationSelector.addQueue(player.uniqueId) { p, loc ->
            val b = if (settings.assignPieceMode) {
                FrameFiller(loc.add(.0, 1.0, .0), xColumn, yRow).placeItemFrame()
            } else {
                FrameFiller(loc.add(.0, 1.0, .0), xColumn, yRow).set(finalStacks)
            }

            if (b) {
                p.sendMessage("一部のピースの設置に失敗しました。平らな場所でもう一度試してください。")
            } else {
                p.sendMessage("パズルの開始位置を設定しました")
            }
        }
    }

    /**
     * プレイヤーに割り当てて、スタックを渡す。(インベントリに入りきらなければドロップする)
     * @return if success
     */
    private fun assignToPlayers(pieces: List<ItemStack>, players: List<Player>): Boolean {
        if (players.isEmpty()) {
            Bukkit.broadcast(text("割り当て先のプレイヤーがみつかりませんでした(ゲームモードを変更してください)", NamedTextColor.RED))
            return false
        }
        pieces.chunked((pieces.size.toDouble() / players.size.toDouble()).toInt()).forEachIndexed { index, list ->
            if (index <= players.lastIndex) {
                val player = players[index]
                player.inventory.clear()
                list.forEach { map ->
                    MapAssigner.assign(map, player, true)
                    val notAdded = player.inventory.addItem(map)
                    notAdded.forEach { (_, u) ->
                        player.world.dropItem(player.location, u)
                    }
                }
            } else {
                // あまりの分
                list.forEach { map ->
                    val player = players.random()
                    MapAssigner.assign(map, player, true)
                    val notAdded = player.inventory.addItem(map)
                    notAdded.forEach { (_, u) ->
                        player.world.dropItem(player.location, u)
                    }
                }
            }
        }

        return true
    }
}