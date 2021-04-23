package com.minexd.vouchers.command

import com.minexd.vouchers.Voucher
import com.minexd.vouchers.menu.EditVoucherMenu
import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import org.bukkit.entity.Player

object VoucherEditCommand {

    @Command(
        names = ["voucher edit", "vouchers edit"],
        description = "Opens the Voucher Editor for a specific Voucher",
        permission = "vouchers.admin"
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "voucher") voucher: Voucher) {
        EditVoucherMenu(voucher).openMenu(player)
    }

}