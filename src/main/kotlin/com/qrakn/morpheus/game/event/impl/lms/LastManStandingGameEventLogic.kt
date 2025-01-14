package com.qrakn.morpheus.game.event.impl.lms

import com.qrakn.morpheus.game.Game
import com.qrakn.morpheus.game.GameState
import com.qrakn.morpheus.game.util.GameEventCountdown
import com.qrakn.morpheus.game.util.team.GameTeam
import com.qrakn.morpheus.game.util.team.GameTeamEventLogic
import net.frozenorb.potpvp.PotPvPND
import org.apache.commons.lang.StringUtils
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Statistic
import org.bukkit.scheduler.BukkitRunnable

open class LastManStandingGameEventLogic(val game: Game) : GameTeamEventLogic(game) {

    override fun start() {
        super.start()

        participants.forEachIndexed { index, team ->
            team.starting = true
            for (player in team.players) {
                player.inventory.clear()
                player.setStatistic(Statistic.PLAYER_KILLS, 0)
                player.teleport(game.arena.eventSpawns[index])
            }
        }

        val kit = game.getParameter(LastManStandingGameKitParameter.LastManStandingGameKitOption::class.java)
        GameEventCountdown(5,
                object : BukkitRunnable() {
                    override fun run() {
                        for (participant in participants) {
                            participant.starting = false
                            participant.fighting = true

                            if (kit != null && kit is LastManStandingGameKitParameter.LastManStandingGameKitOption) {
                                participant.players.forEach { kit.apply(it) }
                            }
                        }
                    }
                }, *participants.toTypedArray())
    }

    fun check() {
        val alive = ArrayList<GameTeam>()

        for (team in participants) {
            if (!team.isFinished()) {
                alive.add(team)
            }
        }

        if (alive.size == 1) {
            val winner = alive[0]
            broadcastWinner(winner)
            end()
        } else if (alive.size == 0) {
            end()
        }
    }

    open fun end() {
        game.end()

        for (player in game.players) {
            player.setStatistic(Statistic.PLAYER_KILLS, 0)
        }
    }

    fun getPlayersLeft(): Int {
        if (game.state == GameState.STARTING) return game.players.size

        var toReturn = 0

        for (participant in participants) {
            for (player in participant.players) {
                if (!(participant.hasDied(player))) {
                    toReturn ++
                }
            }
        }

        return toReturn
    }

    open fun broadcastWinner(winner: GameTeam) {
        for (player in Bukkit.getOnlinePlayers()) {
            player.sendMessage(arrayOf("",
                    ChatColor.GRAY.toString() + "███████",
                    ChatColor.GRAY.toString() + "█" + PotPvPND.getInstance().dominantColor + "█████" + ChatColor.GRAY + "█" + " " + PotPvPND.getInstance().dominantColor + "[${game.event.getName()} Event Winner]",
                    ChatColor.GRAY.toString() + "█" + PotPvPND.getInstance().dominantColor + "█" + ChatColor.GRAY + "█████" + " ",
                    ChatColor.GRAY.toString() + "█" + PotPvPND.getInstance().dominantColor + "████" + ChatColor.GRAY + "██" + " " + winner.getName() + ChatColor.GRAY + " has won the event!",
                    ChatColor.GRAY.toString() + "█" + PotPvPND.getInstance().dominantColor + "█" + ChatColor.GRAY + "█████" + " ",
                    ChatColor.GRAY.toString() + "█" + PotPvPND.getInstance().dominantColor + "█████" + ChatColor.GRAY + "█" + " " + ChatColor.GRAY + ChatColor.ITALIC + "Event Type: (" + StringUtils.join(game.parameters.map { it.getDisplayName() }, ", ") + ")",
                    ChatColor.GRAY.toString() + "███████",
                    "")
            )
        }
    }

}