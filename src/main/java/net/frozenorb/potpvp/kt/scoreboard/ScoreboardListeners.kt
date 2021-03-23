package net.frozenorb.potpvp.kt.scoreboard

import net.frozenorb.potpvp.PotPvPND
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class ScoreboardListeners : Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        PotPvPND.getInstance().scoreboardEngine.create(event.player)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        PotPvPND.getInstance().scoreboardEngine.remove(event.player)
    }

}