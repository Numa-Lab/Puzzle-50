package net.numalab.puzzle.gui

import com.github.bun133.guifly.gui
import com.github.bun133.guifly.gui.GUI
import com.github.bun133.guifly.gui.type.InventoryType
import com.github.bun133.guifly.item
import com.github.bun133.guifly.title
import com.github.bun133.guifly.type
import com.github.bun133.guifly.value.EnumValueItemBuilder
import com.github.bun133.guifly.value.Value
import net.kyori.adventure.text.Component
import net.numalab.puzzle.gen.DefaultPuzzleGenerator
import net.numalab.puzzle.gen.PuzzleGenerateSetting
import net.numalab.puzzle.gen.PuzzleGenerator
import net.numalab.puzzle.img.ImageLoader
import net.numalab.puzzle.img.ImageResizer
import net.numalab.puzzle.img.ImageSplitter
import net.numalab.puzzle.puzzle.ImagedPuzzle
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import java.net.URL
import kotlin.math.ceil
import kotlin.math.roundToInt

class PuzzleSetUpGUI(val url: URL) {
    val sizeMaterialMap = mutableMapOf(
        PuzzleSizeSetting.`100%` to Material.STONE,
        PuzzleSizeSetting.`75%` to Material.COBBLESTONE,
        PuzzleSizeSetting.`50%` to Material.DIRT,
        PuzzleSizeSetting.`25%` to Material.SAND,
        PuzzleSizeSetting.`10%` to Material.GRAVEL,
        PuzzleSizeSetting.`150%` to Material.SANDSTONE,
        PuzzleSizeSetting.`200%` to Material.SANDSTONE
    )
    val sizeValue = Value(PuzzleSizeSetting.`100%`)
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

        val gui = gui(plugin) {
            title(Component.text("パズル設定"))
            type(InventoryType.CHEST_3)
            addItem(size)

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
        val img = ImageLoader().loadImage(url)
        if (img == null) {
            player.sendMessage("画像の読み込みに失敗しました")
            return
        }

        val resizedImage =
            ImageResizer().resize(
                img, sizeValue.value.apply(img.width.toDouble()).toInt(),
                sizeValue.value.apply(img.height.toDouble()).toInt()
            )

        val split = ImageSplitter().split(resizedImage, 128, 128)
        val xColumn = ceil(resizedImage.width / 128.0).toInt()
        val yRow = ceil(resizedImage.height / 128.0).toInt()
        val mock =
            DefaultPuzzleGenerator().generate(PuzzleGenerateSetting(xColumn, yRow, true))

        val imagedPuzzle = ImagedPuzzle(mock, split)

        val stacks = imagedPuzzle.toItemStacks(player.world)

        player.sendMessage("パズルを作成しました")
        for (stack in stacks) {
            player.world.dropItem(player.location, stack)
        }
    }
}