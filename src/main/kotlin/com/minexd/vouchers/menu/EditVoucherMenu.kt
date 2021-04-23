package com.minexd.vouchers.menu

import com.minexd.vouchers.Voucher
import com.minexd.vouchers.VoucherHandler
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.buttons.GlassButton
import net.evilblock.cubed.menu.menus.SelectItemStackMenu
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.prompt.InputPrompt
import net.evilblock.cubed.util.text.TextSplitter
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class EditVoucherMenu(private val voucher: Voucher) : Menu() {

    init {
        updateAfterClick = true
    }

    override fun getTitle(player: Player): String {
        return "Edit Voucher - ${voucher.displayName}"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        return hashMapOf<Int, Button>().also { buttons ->
            buttons[0] = EditNameButton()
            buttons[2] = EditItemButton()
            buttons[4] = EditCommandsButton()
            buttons[6] = ToggleItemMismatchButton()
            buttons[8] = ToggleDisabledButton()

            for (i in 0 until 9) {
                if (!buttons.containsKey(i)) {
                    buttons[i] = GlassButton(7)
                }
            }
        }
    }

    override fun onClose(player: Player, manualClose: Boolean) {
        if (manualClose) {
            Tasks.delayed(1L) {
                VouchersEditorMenu().openMenu(player)
            }
        }
    }

    private inner class EditNameButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Edit Name"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                desc.add("")
                desc.addAll(TextSplitter.split(text = ""))
                desc.add("")
                desc.add(styleAction(ChatColor.GREEN, "LEFT-CLICK", "to edit name"))
            }
        }

        override fun getMaterial(player: Player): Material {
            return Material.NAME_TAG
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                InputPrompt()
                    .withText("${ChatColor.GREEN}Please input a new display name for the voucher.")
                    .acceptInput { input ->
                        voucher.displayName = ChatColor.translateAlternateColorCodes('&', input)

                        Tasks.async {
                            VoucherHandler.saveData()
                        }

                        this@EditVoucherMenu.openMenu(player)
                    }
                    .start(player)
            }
        }
    }

    private inner class EditItemButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Edit Item"
        }

        override fun getMaterial(player: Player): Material {
            return Material.NETHER_STAR
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                desc.add("")
                desc.addAll(TextSplitter.split(text = "Change the item that a player receives when this voucher is given to them."))
                desc.add("")
                desc.add(styleAction(ChatColor.GREEN, "LEFT-CLICK", "to edit item"))
            }
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                SelectItemStackMenu { itemStack ->
                    voucher.itemStack = itemStack.clone()

                    Tasks.async {
                        VoucherHandler.saveData()
                    }

                    this@EditVoucherMenu.openMenu(player)
                }.openMenu(player)
            }
        }
    }

    private inner class EditCommandsButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Edit Commands"
        }

        override fun getMaterial(player: Player): Material {
            return Material.COMMAND
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                desc.add("")
                desc.addAll(TextSplitter.split(text = "Edit the commands that are executed by console when a player redeems this voucher."))
                desc.add("")
                desc.add(styleAction(ChatColor.GREEN, "LEFT-CLICK", "to edit commands"))
            }
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                EditVoucherCommandsMenu(voucher).openMenu(player)
            }
        }
    }

    private inner class ToggleDisabledButton : Button() {
        override fun getName(player: Player): String {
            return buildString {
                append("${ChatColor.AQUA}${ChatColor.BOLD}Toggle Voucher ")
                append("${ChatColor.GRAY}(")

                if (voucher.disabled) {
                    append("${ChatColor.RED}disabled")
                } else {
                    append("${ChatColor.GREEN}enabled")
                }

                append("${ChatColor.GRAY})")
            }
        }

        override fun getMaterial(player: Player): Material {
            return Material.LEVER
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                desc.add("")
                desc.addAll(TextSplitter.split(text = "Toggle if this voucher can be redeemed."))
                desc.add("")
                desc.add(styleAction(ChatColor.GREEN, "LEFT-CLICK", "to toggle voucher"))
            }
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                voucher.disabled = !voucher.disabled

                Tasks.async {
                    VoucherHandler.saveData()
                }
            }
        }
    }

    private inner class ToggleItemMismatchButton : Button() {
        override fun getName(player: Player): String {
            return buildString {
                append("${ChatColor.AQUA}${ChatColor.BOLD}Toggle Item Mismatch ")
                append("${ChatColor.GRAY}(")

                if (!voucher.allowItemMismatch) {
                    append("${ChatColor.RED}not allowed")
                } else {
                    append("${ChatColor.GREEN}allowed")
                }

                append("${ChatColor.GRAY})")
            }
        }

        override fun getMaterial(player: Player): Material {
            return Material.LEVER
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                desc.add("")
                desc.addAll(TextSplitter.split(text = "Optionally allow this voucher to be redeemed if the item has an ID attached but isn't the correct item."))
                desc.add("")
                desc.add(styleAction(ChatColor.GREEN, "LEFT-CLICK", "to toggle item mismatch"))
            }
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                voucher.allowItemMismatch = !voucher.allowItemMismatch

                Tasks.async {
                    VoucherHandler.saveData()
                }
            }
        }
    }

}