package com.minexd.vouchers.command

import com.minexd.vouchers.Voucher
import com.minexd.vouchers.VoucherHandler
import com.minexd.vouchers.menu.EditVoucherMenu
import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.cubed.util.bukkit.Tasks
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object VoucherCreateCommand {

    @Command(
        names = ["voucher create", "vouchers create"],
        description = "Creates a new Voucher from the item in your hand",
        permission = "vouchers.admin"
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "id") id: String) {
        if (player.inventory.itemInHand == null) {
            player.sendMessage("${ChatColor.RED}You must be holding the voucher item in your hand!")
            return
        }

        if (VoucherHandler.getVoucherById(id) != null) {
            player.sendMessage("${ChatColor.RED}That ID is already taken by another voucher!")
            return
        }

        val voucher = Voucher(id, player.inventory.itemInHand)
        VoucherHandler.trackVoucher(voucher)

        Tasks.async {
            VoucherHandler.saveData()
        }

        EditVoucherMenu(voucher).openMenu(player)
    }

}