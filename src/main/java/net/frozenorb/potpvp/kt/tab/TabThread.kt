package net.frozenorb.potpvp.kt.tab

import net.frozenorb.potpvp.PotPvPND
import org.bukkit.Bukkit

class TabThread : Thread("stark - Tab Thread") {

    private val protocolLib = Bukkit.getServer().pluginManager.getPlugin("ProtocolLib")

    init {
        this.isDaemon = true
    }

    override fun run() {
        while (PotPvPND.getInstance().isEnabled && protocolLib != null && protocolLib.isEnabled) {
            for (online in PotPvPND.getInstance().server.onlinePlayers) {
                try {
                    PotPvPND.getInstance().tabEngine.updatePlayer(online)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

            try {
                sleep(250L)
            } catch (e2: InterruptedException) {
                e2.printStackTrace()
            }

        }
    }

}