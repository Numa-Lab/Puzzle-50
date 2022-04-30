package net.numalab.puzzle.setup

import com.github.bun133.bukkitfly.component.text
import com.github.bun133.bukkitfly.stack.addOrDrop
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.numalab.puzzle.gen.DefaultPuzzleGenerator
import net.numalab.puzzle.gen.PuzzleGenerateSetting
import net.numalab.puzzle.geo.FrameFiller
import net.numalab.puzzle.img.ImageLoader
import net.numalab.puzzle.img.ImageResizer
import net.numalab.puzzle.img.ImageSplitter
import net.numalab.puzzle.map.ImagedMap
import net.numalab.puzzle.map.assign.MapAssigner
import net.numalab.puzzle.puzzle.ImagedPuzzle
import net.numalab.puzzle.team.TeamSessionData
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.awt.image.BufferedImage
import java.util.*
import kotlin.math.ceil
import kotlin.math.max

class PuzzleSetupper {
    fun setUp(settings: PuzzleSettings) {
        val player = settings.setter
        if (player == null) {
            println("[ERROR] player is null")
            return
        }

        if (settings.targetPlayers.isEmpty()) {
            player.sendMessage(text("チームが登録されていません", NamedTextColor.RED))
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

        val imagedPuzzles = settings.targetPlayers.associateWith {
            val mock =
                DefaultPuzzleGenerator().generate(PuzzleGenerateSetting(xColumn, yRow, false))
            val imaged = split.map {
                ImagedMap(it.value, mock[it.key.first, it.key.second]!!)
            }

            return@associateWith ImagedPuzzle(mock, imaged)
        }

        if (isShuffle) {
            imagedPuzzles.values.forEach {
                it.shuffle()
            }
        }

        val stacksMap = imagedPuzzles.map {
            return@map it.key to it.value.toItemStacks(player.world)
        }.toMap()

        val finalStacksMap = if (isShuffle) {
            stacksMap.map {
                return@map it.key to it.value.shuffled()
            }.toMap()
        } else {
            stacksMap
        }

        player.sendMessage(Component.empty())

        player.sendMessage("" + ChatColor.GREEN + "パズルを作成しました")
        player.sendMessage("" + ChatColor.GREEN + "縦:${xColumn} 横:${yRow}の計${xColumn * yRow}ピースです")

        player.sendMessage(Component.empty())

        if (settings.assignPieceMode) {
            player.sendMessage("" + ChatColor.GREEN + "ピース割り当て中...")
            settings.targetPlayers.forEach { target ->
                val f = finalStacksMap[target]!!
                if (!giveToPlayers(f, target, true)) {
                    return
                }
                imagedPuzzles[target]!!.puzzle.attributes.add(settings.quitSettingMode)
            }

            when (settings.quitSettingMode) {
                QuitSetting.AssignToAll -> {
                    player.sendMessage(text("退出時に再割り当てが行われます", NamedTextColor.DARK_RED))
                }
                QuitSetting.None -> {
                    player.sendMessage(text("退出時に再割り当ては行われません", NamedTextColor.DARK_RED))
                }
            }
            player.sendMessage(Component.empty())
        } else if (!settings.toSetUpFrame) {
            // ピース割り当てモードでない場合 && フレーム設定モードでない場合
            // → ランダムにピースを与える
            player.sendMessage("" + ChatColor.GREEN + "ピース配布中...")
            settings.targetPlayers.forEach { target ->
                val f = finalStacksMap[target]!!
                if (!giveToPlayers(f, target, false)) {
                    return
                }
            }
            player.sendMessage(Component.empty())
        }

        // パズルのAttributeにTeamSession情報を追加
        if (settings.targetPlayers.size != 1) {
            val sessionID = UUID.randomUUID()

            imagedPuzzles.forEach {
                it.value.puzzle.attributes.add(TeamSessionData(sessionID, it.key))
            }
        }


        // フレームを生成するかどうか
        if (settings.toSetUpFrame) {
            sendPlaceMessage(player, settings.targetPlayers.size - 1, settings.targetPlayers.size)

            settings.locationSelector.addQueue(
                player.uniqueId,
                settings.targetPlayers.size
            ) { p, loc, remainTimes: Int ->
                val b = if (settings.assignPieceMode) {
                    FrameFiller(loc.add(.0, 1.0, .0), xColumn, yRow).placeItemFrame()
                } else {
                    FrameFiller(
                        loc.add(.0, 1.0, .0),
                        xColumn,
                        yRow
                    ).set(finalStacksMap.entries.toList()[settings.targetPlayers.size - remainTimes - 1].value)
                }

                if (!b) {
                    p.sendMessage("一部のピースの設置に失敗しました。平らな場所でもう一度試してください。")
                    return@addQueue false
                } else {
                    p.sendMessage("パズルの開始位置を設定しました")
                    if (remainTimes > 0) {
                        sendPlaceMessage(
                            player,
                            settings.targetPlayers.size - remainTimes + 1,
                            settings.targetPlayers.size
                        )
                    }
                    return@addQueue true
                }
            }
        }
    }

    /**
     * プレイヤーに割り当てて、スタックを渡す。(インベントリに入りきらなければドロップする)
     * @return if success
     */
    private fun giveToPlayers(pieces: List<ItemStack>, players: List<Player>, isAssignMode: Boolean): Boolean {
        if (players.isEmpty()) {
            Bukkit.broadcast(text("割り当て先のプレイヤーがみつかりませんでした(ゲームモードを変更してください)", NamedTextColor.RED))
            return false
        }

        players.forEach { it.inventory.clear() }

        pieces.chunked(max((pieces.size.toDouble() / players.size.toDouble()).toInt(), 1))
            .forEachIndexed { index, list ->
                if (index <= players.lastIndex) {
                    val player = players[index]
                    list.forEach { map ->
                        if (isAssignMode) {
                            MapAssigner.assign(map, player, true)
                        }
                        player.inventory.addOrDrop(map)
                    }
                } else {
                    // あまりの分
                    list.forEach { map ->
                        val player = players.random()
                        if (isAssignMode) {
                            MapAssigner.assign(map, player, true)
                        }
                        player.inventory.addOrDrop(map)
                    }
                }
            }

        return true
    }

    private fun sendPlaceMessage(player: Player, remainTimes: Int, allTimes: Int) {
        when (allTimes) {
            1 -> {
                player.sendMessage(text("ブロックをクリックして開始位置を指定してください", NamedTextColor.GREEN))
            }

            else -> {
                when (remainTimes) {
                    0 -> {

                    }
                    else -> {
                        player.sendMessage(
                            text(
                                "ブロックをクリックして開始位置を指定してください[${remainTimes}/${allTimes}]",
                                NamedTextColor.GREEN
                            )
                        )
                    }
                }
            }
        }
    }
}