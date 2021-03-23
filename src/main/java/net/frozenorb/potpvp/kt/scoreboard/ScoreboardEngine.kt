package net.frozenorb.potpvp.kt.scoreboard

import net.frozenorb.potpvp.PotPvPND
import org.bukkit.entity.Player

class ScoreboardEngine {
    private val boards: HashMap<String, Scoreboard> = HashMap()
    var configuration: ScoreboardConfiguration? = null
    var updateInterval: Int = 2

    fun load() {
        ScoreboardThread().start()
        PotPvPND.getInstance().server.pluginManager.registerEvents(ScoreboardListeners(), PotPvPND.getInstance())
    }

    internal fun create(player: Player) {
        if (configuration != null) {
            boards[player.name] = Scoreboard(player)
        }
    }

    internal fun updateScoreboard(player: Player) {
        boards[player.name]?.update()
    }

    internal fun remove(player: Player) {
        boards.remove(player.name)
    }
}