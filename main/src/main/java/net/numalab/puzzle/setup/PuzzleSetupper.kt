package net.numalab.puzzle.setup

import net.kyori.adventure.text.Component
import net.numalab.puzzle.gen.DefaultPuzzleGenerator
import net.numalab.puzzle.gen.PuzzleGenerateSetting
import net.numalab.puzzle.img.ImageLoader
import net.numalab.puzzle.img.ImageResizer
import net.numalab.puzzle.img.ImageSplitter
import net.numalab.puzzle.map.ImagedMap
import net.numalab.puzzle.puzzle.ImagedPuzzle
import org.bukkit.ChatColor
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
        player.sendMessage("" + ChatColor.GREEN + "画像読み込み完了")

        player.sendMessage("" + ChatColor.GREEN + "画像リサイズ中...")
        val resizedImage =
            ImageResizer().resize(
                img, sizeValue.apply(img.width.toDouble()).toInt(),
                sizeValue.apply(img.height.toDouble()).toInt()
            )
        player.sendMessage("" + ChatColor.GREEN + "画像リサイズ完了")

        player.sendMessage("" + ChatColor.GREEN + "画像分割中...")
        val split = ImageSplitter().split(resizedImage, 128, 128)
        player.sendMessage("" + ChatColor.GREEN + "画像分割完了")
        val xColumn = ceil(resizedImage.width / 128.0).toInt()
        val yRow = ceil(resizedImage.height / 128.0).toInt()

        player.sendMessage("" + ChatColor.GREEN + "パズル生成中...")
        val mock =
            DefaultPuzzleGenerator().generate(PuzzleGenerateSetting(xColumn, yRow, false))
        val imaged = split.map {
            ImagedMap(it.value, mock[it.key.first, it.key.second]!!)
        }
        player.sendMessage("" + ChatColor.GREEN + "パズル生成完了")

        val imagedPuzzle = ImagedPuzzle(mock, imaged)

        player.sendMessage("" + ChatColor.GREEN + "ピース生成中...")
        val stacks = imagedPuzzle.toItemStacks(player.world)
        player.sendMessage("" + ChatColor.GREEN + "ピース生成完了")

        player.sendMessage("" + ChatColor.GREEN + "ピースシャッフル中...")
        val finalStacks = if (isShuffle) {
            stacks.shuffled()
        } else {
            stacks
        }
        player.sendMessage("" + ChatColor.GREEN + "ピースシャッフル完了")

        player.sendMessage(Component.empty())

        player.sendMessage("" + ChatColor.GREEN + "パズルを作成しました")
        player.sendMessage("" + ChatColor.GREEN + "縦:${yRow} 横:${xColumn}の計${xColumn * yRow}ピースです")

        player.sendMessage(Component.empty())

        player.sendMessage("" + ChatColor.GREEN + "ブロックをクリックして開始位置を指定してください")

        settings.locationSelector.addQueue(player.uniqueId, finalStacks, xColumn, yRow)
    }
}