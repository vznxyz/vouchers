package com.minexd.vouchers

import com.google.common.io.Files
import com.google.gson.reflect.TypeToken
import net.evilblock.cubed.backup.BackupHandler
import net.evilblock.cubed.logging.LogFile
import net.evilblock.cubed.logging.LogHandler
import net.evilblock.cubed.serializers.Serializers
import java.io.File
import java.lang.reflect.Type
import java.util.concurrent.ConcurrentHashMap

object VoucherHandler {

    private val dataType: Type = object : TypeToken<List<Voucher>>() {}.type
    private val vouchers: MutableMap<String, Voucher> = ConcurrentHashMap()

    private var loaded: Boolean = false

    val logFile: LogFile = LogFile(File(Vouchers.instance.dataFolder, "logs.txt"))

    private fun getInternalDataFile(): File {
        return File(Vouchers.instance.dataFolder, "vouchers.json")
    }

    fun initialLoad() {
        val dataFile = getInternalDataFile()
        if (dataFile.exists()) {
            val backupFile = BackupHandler.findNextBackupFile("vouchers")
            Files.copy(dataFile, backupFile)

            Files.newReader(dataFile, Charsets.UTF_8).use { reader ->
                val data = Serializers.gson.fromJson(reader.readLine(), dataType) as List<Voucher>

                for (voucher in data) {
                    trackVoucher(voucher)
                }
            }
        }

        loaded = true

        LogHandler.trackLogFile(logFile)
    }

    fun saveData() {
        if (loaded) {
            Files.write(Serializers.gson.toJson(vouchers.values, dataType), getInternalDataFile(), Charsets.UTF_8)
        }
    }

    fun getVouchers(): Collection<Voucher> {
        return vouchers.values
    }

    fun getVoucherById(id: String): Voucher? {
        return vouchers[id.toLowerCase()]
    }

    fun trackVoucher(voucher: Voucher) {
        vouchers[voucher.id.toLowerCase()] = voucher
    }

    fun forgetVoucher(voucher: Voucher) {
        vouchers.remove(voucher.id.toLowerCase())
    }

}