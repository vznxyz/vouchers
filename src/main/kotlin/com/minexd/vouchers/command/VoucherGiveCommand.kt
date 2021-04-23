package com.minexd.vouchers.command

import com.minexd.vouchers.Voucher
import com.minexd.vouchers.VoucherHandler
import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object VoucherGiveCommand {

    @Command(
        names = ["voucher give", "vouchers give"],
        description = "Gives a player a Voucher",
        permission = "vouchers.give"
    )
    @JvmStatic
    fun execute(
        sender: CommandSender,
        @Param(name = "player") player: Player,
        @Param(name = "voucher") voucher: Voucher,
        @Param(name = "amount") amount: Int
    ) {
        if (sender is Player) {
            player.sendMessage("${ChatColor.GREEN}You've been given a ${voucher.displayName}${ChatColor.GREEN}!")
        } else {
            player.sendMessage("${ChatColor.GREEN}You've been given a ${voucher.displayName}${ChatColor.GREEN}!")
        }

        try {
            voucher.give(player, amount)
            sender.sendMessage("${ChatColor.GREEN}You've given a ${voucher.displayName} ${ChatColor.GRAY}(Voucher) ${ChatColor.GREEN}to ${player.name}!")
            VoucherHandler.logFile.commit("${sender.name} gave ${amount}x ${voucher.id} voucher(s) to ${player.name} (${player.uniqueId})")
        } catch (e: Exception) {
            e.printStackTrace()
            sender.sendMessage("${ChatColor.RED}Failed to give a ${voucher.displayName} ${ChatColor.GRAY}(Voucher) ${ChatColor.RED}to ${player.name}!")
            VoucherHandler.logFile.commit("${sender.name} failed to give ${amount}x ${voucher.id} voucher(s) to ${player.name} (${player.uniqueId})")
        }
    }

}