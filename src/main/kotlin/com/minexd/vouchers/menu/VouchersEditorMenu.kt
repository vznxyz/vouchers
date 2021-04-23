package com.minexd.vouchers.menu

import com.minexd.vouchers.Voucher
import com.minexd.vouchers.VoucherHandler
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.buttons.AddButton
import net.evilblock.cubed.menu.buttons.GlassButton
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.cubed.menu.menus.SelectItemStackMenu
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.prompt.InputPrompt
import net.evilblock.cubed.util.text.TextSplitter
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack

class VouchersEditorMenu : PaginatedMenu() {

    init {
        updateAfterClick = true
    }

    override fun getPrePaginatedTitle(player: Player): String {
        return "Edit Vouchers"
    }

    override fun getGlobalButtons(player: Player): Map<Int, Button> {
        return hashMapOf<Int, Button>().also { buttons ->
            for (i in 9..17) {
                buttons[i] = GlassButton(0)
            }

            buttons[2] = CreateVoucherButton()
        }
    }

    override fun getAllPagesButtons(player: Player): Map<Int, Button> {
        return hashMapOf<Int, Button>().also { buttons ->
            for (voucher in VoucherHandler.getVouchers()) {
                buttons[buttons.size] = VoucherButton(voucher)
            }
        }
    }

    override fun getButtonsStartOffset(): Int {
        return 9
    }

    override fun getMaxItemsPerPage(player: Player): Int {
        return 45
    }

    override fun size(buttons: Map<Int, Button>): Int {
        return 54
    }

    private inner class CreateVoucherButton : AddButton() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Create New Voucher"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                desc.add("")
                desc.addAll(TextSplitter.split(text = "Create a new voucher by completing the setup procedure."))
                desc.add("")
                desc.add(styleAction(ChatColor.GREEN, "LEFT-CLICK", "to create a new voucher"))
            }
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                InputPrompt()
                    .withText("${ChatColor.GREEN}Please input an ID for the voucher.")
                    .acceptInput { input ->
                        if (VoucherHandler.getVoucherById(input) != null) {
                            player.sendMessage("${ChatColor.RED}That ID is already taken by another voucher!")
                            return@acceptInput
                        }

                        SelectItemStackMenu { itemStack ->
                            val voucher = Voucher(input, itemStack)
                            VoucherHandler.trackVoucher(voucher)

                            Tasks.async {
                                VoucherHandler.saveData()
                            }

                            EditVoucherMenu(voucher).openMenu(player)
                        }.openMenu(player)
                    }
                    .start(player)
            }
        }
    }

    private inner class VoucherButton(private val voucher: Voucher) : Button() {
        override fun getName(player: Player): String {
            return voucher.displayName
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                if (voucher.itemStack.hasItemMeta() && voucher.itemStack.itemMeta.hasLore()) {
                    desc.addAll(voucher.itemStack.itemMeta.lore)
                }

                desc.add("")
                desc.add("${ChatColor.GRAY}(ID: ${voucher.id})")
                desc.add("")
                desc.add("${ChatColor.YELLOW}${ChatColor.BOLD}Commands")

                if (voucher.commands.isEmpty()) {
                    desc.add(" ${ChatColor.GRAY}None")
                } else {
                    for (command in voucher.commands) {
                        desc.add(" ${ChatColor.WHITE}$command")
                    }
                }

                desc.add("")
                desc.add(styleAction(ChatColor.GREEN, "LEFT-CLICK", "to edit voucher"))
                desc.add(styleAction(ChatColor.RED, "RIGHT-CLICK", "to delete voucher"))
            }
        }

        override fun getButtonItem(player: Player): ItemStack {
            return ItemBuilder.copyOf(voucher.itemStack)
                .name(getName(player))
                .setLore(getDescription(player))
                .build()
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                EditVoucherMenu(voucher).openMenu(player)
            } else if (clickType.isRightClick) {
                ConfirmMenu { confirmed ->
                    if (confirmed) {
                        VoucherHandler.forgetVoucher(voucher)

                        Tasks.async {
                            VoucherHandler.saveData()
                        }

                        this@VouchersEditorMenu.openMenu(player)
                    }
                }.openMenu(player)
            }
        }
    }

}