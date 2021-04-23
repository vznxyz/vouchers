package com.minexd.vouchers

import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.ItemUtils
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class Voucher(val id: String, var itemStack: ItemStack) {

    var displayName: String = id.toLowerCase()
    var commands: List<String> = arrayListOf()

    var disabled: Boolean = false
    var allowItemMismatch: Boolean = false

    fun give(player: Player, amount: Int) {
        val item = ItemBuilder.copyOf(itemStack)
            .name(displayName)
            .amount(amount)
            .build()

        player.inventory.addItem(ItemUtils.addToItemTag(item, "VoucherItem", id, preserve = true))
        player.updateInventory()
    }

    fun redeem(player: Player) {
        for (command in commands) {
            val processedCommand = command
                .replace("{playerUUID}", player.uniqueId.toString())
                .replace("{playerName}", player.name)
                .replace("{playerDisplayName}", player.displayName)
                .replace("{voucherID}", id)
                .replace("{voucherDisplayName}", displayName)

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), processedCommand)
        }

        VoucherHandler.logFile.commit("${player.name} (${player.uniqueId}) redeemed a $id voucher")
    }

}