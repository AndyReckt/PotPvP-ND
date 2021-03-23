package net.frozenorb.potpvp.kt.scoreboard

import net.frozenorb.potpvp.PotPvPND

class ScoreboardThread : Thread("stark - Scoreboard Thread") {

    init {
        this.isDaemon = true
    }

    override fun run() {
        while (true) {
            for (online in PotPvPND.getInstance().server.onlinePlayers) {
                try {
                    PotPvPND.getInstance().scoreboardEngine.updateScoreboard(online)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

            try {
                sleep(PotPvPND.getInstance().scoreboardEngine.updateInterval * 50L)
            } catch (e2: InterruptedException) {
                e2.printStackTrace()
            }

        }
    }

}