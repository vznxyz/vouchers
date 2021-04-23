package com.minexd.vouchers.menu

import com.minexd.vouchers.Voucher
import com.minexd.vouchers.VoucherHandler
import net.evilblock.cubed.menu.menus.TextEditorMenu
import net.evilblock.cubed.util.bukkit.Tasks
import org.bukkit.entity.Player

class EditVoucherCommandsMenu(private val voucher: Voucher) : TextEditorMenu(lines = voucher.commands) {

    override fun getPrePaginatedTitle(player: Player): String {
        return "Edit Commands - ${voucher.displayName}"
    }

    override fun onSave(player: Player, list: List<String>) {
        voucher.commands = list

        Tasks.async {
            VoucherHandler.saveData()
        }
    }

    override fun onClose(player: Player) {
        Tasks.delayed(1L) {
            EditVoucherMenu(voucher).openMenu(player)
        }
    }

}