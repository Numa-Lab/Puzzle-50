package net.numalab.puzzle.setup

import com.github.bun133.guifly.gui
import com.github.bun133.guifly.gui.GUI
import com.github.bun133.guifly.gui.type.InventoryType
import com.github.bun133.guifly.item
import com.github.bun133.guifly.title
import com.github.bun133.guifly.type
import com.github.bun133.guifly.value.BooleanValueItemBuilder
import com.github.bun133.guifly.value.EnumValueItemBuilder
import com.github.bun133.guifly.value.Value
import net.kyori.adventure.text.Component
import net.numalab.puzzle.gen.DefaultPuzzleGenerator
import net.numalab.puzzle.gen.PuzzleGenerateSetting
import net.numalab.puzzle.img.ImageLoader
import net.numalab.puzzle.img.ImageResizer
import net.numalab.puzzle.img.ImageSplitter
import net.numalab.puzzle.puzzle.ImagedPuzzle
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import java.net.URL
import kotlin.math.ceil

class PuzzleSetUpGUI(val url: URL, val puzzleLocationSelector: PuzzleLocationSelector) {
    val sizeMaterialMap = mutableMapOf(
        PuzzleSizeSetting.`100%` to Material.STONE,
        PuzzleSizeSetting.`75%` to Material.COBBLESTONE,
        PuzzleSizeSetting.`50%` to Material.DIRT,
        PuzzleSizeSetting.`25%` to Material.SAND,
        PuzzleSizeSetting.`10%` to Material.GRAVEL,
        PuzzleSizeSetting.`150%` to Material.SANDSTONE,
        PuzzleSizeSetting.`200%` to Material.NETHER_GOLD_ORE,
        PuzzleSizeSetting.`300%` to Material.GILDED_BLACKSTONE,
        PuzzleSizeSetting.`400%` to Material.CRYING_OBSIDIAN,
    )
    val sizeValue = Value(PuzzleSizeSetting.`100%`)

    val isShuffle = Value(false)
    fun main(plugin: JavaPlugin): GUI {
        val size = EnumValueItemBuilder(2, 2, sizeValue, sizeMaterialMap.mapValues {
            ItemStack(it.value).also { i ->
                i.editMeta { m ->
                    m.displayName(
                        Component.text(it.key.name)
                    )
                }
            }
        }).markAsUnMovable().build()

        val shuffle = BooleanValueItemBuilder(
            3,
            2,
            isShuffle,
            ItemStack(Material.GRAY_WOOL).also { it.editMeta { m -> m.displayName(Component.text("ピースをシャッフルして初期配置しない")) } },
            ItemStack(Material.LIME_WOOL).also { it.editMeta { m -> m.displayName(Component.text("ピースをシャッフルして初期配置する")) } }
        ).markAsUnMovable().build()

        val gui = gui(plugin) {
            title(Component.text("パズル設定"))
            type(InventoryType.CHEST_3)
            addItem(size)
            addItem(shuffle)

            item(9, 3) {
                markAsUnMovable()
                stack(ItemStack(Material.LIME_WOOL).also { it.editMeta { m -> m.displayName(Component.text("決定")) } })
                click { e ->
                    e.whoClicked.closeInventory()
                    onConfirm(e.whoClicked as Player)
                }
            }
            setMarkedNotInsertable()
        }

        return gui
    }

    fun onConfirm(player: Player) {
        println("size: ${sizeValue.value}")
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
                img, sizeValue.value.apply(img.width.toDouble()).toInt(),
                sizeValue.value.apply(img.height.toDouble()).toInt()
            )
        player.sendMessage("" + ChatColor.GREEN + "画像リサイズ完了")

        player.sendMessage("" + ChatColor.GREEN + "画像分割中...")
        val split = ImageSplitter().split(resizedImage, 128, 128)
        player.sendMessage("" + ChatColor.GREEN + "画像分割完了")
        val xColumn = ceil(resizedImage.width / 128.0).toInt()
        val yRow = ceil(resizedImage.height / 128.0).toInt()

        player.sendMessage("" + ChatColor.GREEN + "パズル生成中...")
        val mock =
            DefaultPuzzleGenerator().generate(PuzzleGenerateSetting(xColumn, yRow, true))
        player.sendMessage("" + ChatColor.GREEN + "パズル生成完了")

        val imagedPuzzle = ImagedPuzzle(mock, split)

        player.sendMessage("" + ChatColor.GREEN + "ピース生成中...")
        val stacks = imagedPuzzle.toItemStacks(player.world)
        player.sendMessage("" + ChatColor.GREEN + "ピース生成完了")

        player.sendMessage("" + ChatColor.GREEN + "ピースシャッフル中...")
        val finalStacks = if (isShuffle.value) {
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

        puzzleLocationSelector.addQueue(player.uniqueId, finalStacks, xColumn, yRow)
    }
}