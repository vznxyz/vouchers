package com.minexd.vouchers

import com.minexd.vouchers.command.VoucherCreateCommand
import com.minexd.vouchers.command.VoucherEditCommand
import com.minexd.vouchers.command.VoucherEditorCommand
import com.minexd.vouchers.command.VoucherGiveCommand
import com.minexd.vouchers.command.parameter.VoucherParameterType
import com.minexd.vouchers.listener.VouchersListeners
import net.evilblock.cubed.command.CommandHandler
import org.bukkit.plugin.java.JavaPlugin

class Vouchers : JavaPlugin() {

    companion object {
        @JvmStatic
        lateinit var instance: Vouchers
    }

    override fun onEnable() {
        super.onEnable()

        instance = this

        CommandHandler.registerParameterType(Voucher::class.java, VoucherParameterType())
        CommandHandler.registerClass(VoucherCreateCommand::class.java)
        CommandHandler.registerClass(VoucherEditCommand::class.java)
        CommandHandler.registerClass(VoucherEditorCommand::class.java)
        CommandHandler.registerClass(VoucherGiveCommand::class.java)

        server.pluginManager.registerEvents(VouchersListeners, this)

        VoucherHandler.initialLoad()
    }

}