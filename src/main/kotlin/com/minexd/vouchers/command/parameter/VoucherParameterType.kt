package com.minexd.vouchers.command.parameter

import com.minexd.vouchers.Voucher
import com.minexd.vouchers.VoucherHandler
import net.evilblock.cubed.command.data.parameter.ParameterType
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class VoucherParameterType : ParameterType<Voucher> {

    override fun transform(sender: CommandSender, source: String): Voucher? {
        val voucher = VoucherHandler.getVoucherById(source)
        if (voucher == null) {
            sender.sendMessage("${ChatColor.RED}Couldn't find a Voucher with an ID of `${ChatColor.WHITE}$source${ChatColor.RED}`!")
        }
        return voucher
    }

    override fun tabComplete(player: Player, flags: Set<String>, source: String): List<String> {
        return arrayListOf<String>().also { completions ->
            for (voucher in VoucherHandler.getVouchers()) {
                if (voucher.id.startsWith(source, ignoreCase = true)) {
                    completions.add(voucher.id)
                }
            }
        }
    }

}