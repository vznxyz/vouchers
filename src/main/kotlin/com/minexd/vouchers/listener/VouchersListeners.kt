package com.minexd.vouchers.listener

import com.minexd.vouchers.VoucherHandler
import net.evilblock.cubed.util.bukkit.ItemUtils
import net.minecraft.server.v1_8_R3.NBTTagString
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

object VouchersListeners : Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onPlayerInteractEvent(event: PlayerInteractEvent) {
        if ((event.action == Action.RIGHT_CLICK_AIR || event.action == Action.RIGHT_CLICK_BLOCK) && event.player.inventory.itemInHand != null && event.player.inventory.itemInHand.type != Material.AIR) {
            val itemInHand = event.player.inventory.itemInHand
            if (ItemUtils.itemTagHasKey(itemInHand, "VoucherItem")) {
                val voucherId = (ItemUtils.readItemTagKey(itemInHand, "VoucherItem") as NBTTagString).a_()
                val voucher = VoucherHandler.getVoucherById(voucherId) ?: return

                if (voucher.disabled) {
                    event.player.sendMessage("${ChatColor.RED}That voucher is currently disabled!")
                    return
                }

                if (!isSimilar(voucher.itemStack, itemInHand) && !voucher.allowItemMismatch) {
                    event.player.sendMessage("${ChatColor.RED}Voucher item mismatch! Please contact a staff member for a new voucher!")
                    return
                }

                if (itemInHand.amount <= 1) {
                    event.player.inventory.itemInHand = null
                } else {
                    itemInHand.amount = itemInHand.amount - 1
                }

                event.player.updateInventory()

                voucher.redeem(event.player)
            }
        }
    }

    /**
     * Copy of [ItemUtils.isSimilar] but without the displayName check.
     */
    private fun isSimilar(first: ItemStack, second: ItemStack): Boolean {
        if (first.type != second.type) {
            return false
        }

        if (first.durability != second.durability) {
            return false
        }

        if (first.hasItemMeta() != second.hasItemMeta()) {
            return false
        }

        if (first.itemMeta.hasLore() != second.itemMeta.hasLore()) {
            return false
        }

        return true
    }

}