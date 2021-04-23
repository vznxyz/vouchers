package com.minexd.vouchers.command

import com.minexd.vouchers.menu.VouchersEditorMenu
import net.evilblock.cubed.command.Command
import org.bukkit.entity.Player

object VoucherEditorCommand {

    @Command(
        names = ["voucher editor", "vouchers editor"],
        description = "Opens the Voucher Editor",
        permission = "vouchers.admin"
    )
    @JvmStatic
    fun execute(player: Player) {
        VouchersEditorMenu().openMenu(player)
    }

}