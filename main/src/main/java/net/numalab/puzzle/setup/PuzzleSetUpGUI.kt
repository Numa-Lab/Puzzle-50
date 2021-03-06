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
import net.numalab.puzzle.img.ImageLoader
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import java.net.URL
import java.util.UUID
import kotlin.math.ceil

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
                        m.displayName(text("?????????????????????????????????????????????????????????"))
                    }
                }
            },
            QuitSetting.None to {
                ItemStack(Material.BARRIER).also {
                    it.editMeta { m ->
                        m.displayName(text("????????????????????????????????????????????????????????????"))
                    }
                }
            }
        )
    }

    private val sizeValue = Value(defaultSetting.size.value())
    private val isShuffle = Value(defaultSetting.isShuffle.value())
    private val isAssign = Value(defaultSetting.isAssign.value())
    private val quitSetting = Value(defaultSetting.quitSettingMode.value())
    private val isTeamMode = Value(defaultSetting.isTeamMode.value())
    private val toSetUpFrame = Value(defaultSetting.toSetUpFrame.value())

    /**
     * returns null if url is not valid image url
     */
    private fun genGUI(plugin: JavaPlugin): GUI? {
        val img = ImageLoader().loadImage(url) ?: return null
        val width = img.width
        val height = img.height


        val size = EnumValueItemBuilder(2, 2, sizeValue, sizeMaterialMap.mapValues {
            ItemStack(it.value).also { i ->
                i.editMeta { m ->
                    m.displayName(
                        Component.text(it.key.name)
                    )

                    if (width != null && height != null) {
                        val x = ceil(it.key.apply(width.toDouble()) / 128.0).toInt()
                        val y = ceil(it.key.apply(height.toDouble()) / 128.0).toInt()

                        m.lore(
                            listOf(
                                Component.text(
                                    "?????????:${x}??${y}"
                                ),
                                Component.text(
                                    "????????????:${x * y}?????????"
                                )
                            )
                        )
                    }
                }
            }
        }).markAsUnMovable().build()

        val shuffle = BooleanValueItemBuilder(
            3,
            2,
            isShuffle,
            ItemStack(Material.GRAY_WOOL).also { it.editMeta { m -> m.displayName(text("????????????????????????????????????")) } },
            ItemStack(Material.LIME_WOOL).also { it.editMeta { m -> m.displayName(text("?????????????????????????????????")) } }
        ).markAsUnMovable().build()

        val assign = BooleanValueItemBuilder(
            4,
            2,
            isAssign,
            ItemStack(Material.GRAY_WOOL).also {
                it.editMeta { m ->
                    m.displayName(text("???????????????????????????????????????"))
                    m.lore(
                        listOf(
                            text("[??????!]?????????????????????", NamedTextColor.RED) + text(
                                "????????????????????????",
                                NamedTextColor.WHITE
                            ).style { s -> s.decorate(TextDecoration.BOLD) })
                    )
                }
            },
            ItemStack(Material.LIME_WOOL).also {
                it.editMeta { m ->
                    m.displayName(text("????????????????????????????????????"))
                    m.lore(
                        listOf(
                            text("[??????!]?????????????????????", NamedTextColor.RED) + text(
                                "????????????????????????",
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

        val team = BooleanValueItemBuilder(
            6,
            2,
            isTeamMode,
            ItemStack(Material.GRAY_WOOL).also { it.editMeta { m -> m.displayName(text("????????????????????????????????????")) } },
            ItemStack(Material.LIME_WOOL).also { it.editMeta { m -> m.displayName(text("?????????????????????????????????")) } }
        ).markAsUnMovable().build()

        val frame = BooleanValueItemBuilder(
            7,
            2,
            toSetUpFrame,
            ItemStack(Material.GRAY_WOOL).also { it.editMeta { m -> m.displayName(text("??????????????????????????????")) } },
            ItemStack(Material.LIME_WOOL).also { it.editMeta { m -> m.displayName(text("???????????????????????????")) } }
        ).markAsUnMovable().build()

        val gui = gui(plugin) {
            title(Component.text("???????????????"))
            type(InventoryType.CHEST_3)
            addItem(size)
            addItem(shuffle)
            addItem(assign)
            addItem(quit)
            addItem(team)
            addItem(frame)

            item(9, 3) {
                markAsUnMovable()
                stack(ItemStack(Material.LIME_WOOL).also { it.editMeta { m -> m.displayName(text("??????")) } })
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
        if (gui != null) {
            gui.open(player)
            callBacks[player.uniqueId] = callBack
        } else {
            player.sendMessage("" + ChatColor.RED + "??????????????????????????????????????????(URL????????????????????????????????????)")
        }
    }

    private fun onConfirm(player: Player) {
        val callBack = callBacks.remove(player.uniqueId) ?: return
        val setting = if (isTeamMode.value) {
            PuzzleSettings(
                puzzleLocationSelector,
                player,
                sizeValue.value,
                isShuffle.value,
                url,
                isAssign.value,
                quitSetting.value,
                defaultSetting.conf.teams(),
                toSetUpFrame.value,
            )
        } else {
            PuzzleSettings(
                puzzleLocationSelector,
                player,
                sizeValue.value,
                isShuffle.value,
                url,
                isAssign.value,
                quitSetting.value,
                listOf("DefaultTeam" to defaultSetting.conf.players()),
                toSetUpFrame.value,
            )
        }
        defaultSetting.applySettings(setting)
        callBack(setting)
    }
}