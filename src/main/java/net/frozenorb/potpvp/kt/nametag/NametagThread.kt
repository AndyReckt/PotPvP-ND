package net.frozenorb.potpvp.kt.nametag

import net.frozenorb.potpvp.PotPvPND
import java.util.concurrent.ConcurrentHashMap

internal class NametagThread : Thread("stark - Nametag Thread") {
    init {
        this.isDaemon = true
    }

    override fun run() {
        while (true) {
            val pendingUpdatesIterator = pendingUpdates.keys.iterator()
            while (pendingUpdatesIterator.hasNext()) {
                val pendingUpdate = pendingUpdatesIterator.next()
                try {
                    PotPvPND.getInstance().nametagEngine.applyUpdate(pendingUpdate)
                    pendingUpdatesIterator.remove()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

            try {
                sleep(PotPvPND.getInstance().nametagEngine.updateInterval * 50L)
            } catch (e2: InterruptedException) {
                e2.printStackTrace()
            }

        }
    }

    companion object {
        var pendingUpdates = ConcurrentHashMap<NametagUpdate, Boolean>()
    }
}