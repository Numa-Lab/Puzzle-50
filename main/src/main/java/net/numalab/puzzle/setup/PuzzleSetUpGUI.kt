package net.numalab.puzzle.setup

import com.github.bun133.bukkitfly.component.plus
import com.github.bun133.bukkitfly.component.text
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
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.numalab.puzzle.DefaultPuzzleSetting
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import java.net.URL
import java.util.UUID

class PuzzleSetUpGUI(
    private val url: URL,
    private val puzzleLocationSelector: PuzzleLocationSelector,
    private val defaultSetting: DefaultPuzzleSetting
) {
    companion object {
        val sizeMaterialMap = mapOf(
            PuzzleSizeSetting.`100%` to Material.STONE,
            PuzzleSizeSetting.`75%` to Material.COBBLESTONE,
            PuzzleSizeSetting.`50%` to Material.DIRT,
            PuzzleSizeSetting.`25%` to Material.SAND,
            PuzzleSizeSetting.`10%` to Material.GRAVEL,
            PuzzleSizeSetting.`150%` to Material.SANDSTONE,
            PuzzleSizeSetting.`200%` to Material.NETHER_GOLD_ORE,
            PuzzleSizeSetting.`300%` to Material.GILDED_BLACKSTONE,
            PuzzleSizeSetting.`400%` to Material.CRYING_OBSIDIAN,
            PuzzleSizeSetting.`500%` to Material.END_STONE,
            PuzzleSizeSetting.`1000%` to Material.BEDROCK,
        )

        val quitItemStackMap = mapOf(
            QuitSetting.AssignToAll to {
                ItemStack(Material.FIREWORK_ROCKET).also {
                    it.editMeta { m ->
                        m.displayName(text("途中抜けした人のピースを再割り当てする"))
                    }
                }
            },
            QuitSetting.None to {
                ItemStack(Material.BARRIER).also {
                    it.editMeta { m ->
                        m.displayName(text("途中抜けした人のピースを再割り当てしない"))
                    }
                }
            }
        )
    }

    private val sizeValue = Value(defaultSetting.size.value())
    private val isShuffle = Value(defaultSetting.isShuffle.value())
    private val isAssign = Value(defaultSetting.isAssign.value())
    private val quitSetting = Value(defaultSetting.quitSettingMode.value())

    private fun genGUI(plugin: JavaPlugin): GUI {
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
            ItemStack(Material.GRAY_WOOL).also { it.editMeta { m -> m.displayName(text("ピースをシャッフルしない")) } },
            ItemStack(Material.LIME_WOOL).also { it.editMeta { m -> m.displayName(text("ピースをシャッフルする")) } }
        ).markAsUnMovable().build()

        val assign = BooleanValueItemBuilder(
            4,
            2,
            isAssign,
            ItemStack(Material.GRAY_WOOL).also { it.editMeta { m -> m.displayName(text("ピースを個人に割り当てない")) } },
            ItemStack(Material.LIME_WOOL).also {
                it.editMeta { m ->
                    m.displayName(text("ピースを個人に割り当てる"))
                    m.lore(
                        listOf(
                            text("[注意!]インベントリが", NamedTextColor.RED) + text(
                                "リセットされます",
                                NamedTextColor.WHITE
                            ).style { s -> s.decorate(TextDecoration.BOLD) })
                    )
                }
            }
        ).markAsUnMovable().build()

        val quit = EnumValueItemBuilder(
            5,
            2,
            quitSetting,
            quitItemStackMap.mapValues { v -> v.value() }
        ).markAsUnMovable().build()

        val gui = gui(plugin) {
            title(Component.text("パズル設定"))
            type(InventoryType.CHEST_3)
            addItem(size)
            addItem(shuffle)
            addItem(assign)
            addItem(quit)

            item(9, 3) {
                markAsUnMovable()
                stack(ItemStack(Material.LIME_WOOL).also { it.editMeta { m -> m.displayName(text("決定")) } })
                click { e ->
                    e.whoClicked.closeInventory()
                    onConfirm(e.whoClicked as Player)
                }
            }
            setMarkedNotInsertable()
        }

        return gui
    }

    private val callBacks = mutableMapOf<UUID, (PuzzleSettings) -> Unit>()

    fun main(plugin: JavaPlugin, player: Player, callBack: (PuzzleSettings) -> Unit) {
        val gui = genGUI(plugin)
        gui.open(player)
        callBacks[player.uniqueId] = callBack
    }

    private fun onConfirm(player: Player) {
        val callBack = callBacks.remove(player.uniqueId) ?: return
        val setting = PuzzleSettings(
            puzzleLocationSelector,
            player,
            sizeValue.value,
            isShuffle.value,
            url,
            isAssign.value,
            quitSetting.value,
            defaultSetting.conf.players()
        )
        defaultSetting.applySettings(setting)
        callBack(setting)
    }
}