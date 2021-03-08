package com.qrakn.morpheus.game.event.impl.t4g

import com.qrakn.morpheus.game.Game
import com.qrakn.morpheus.game.GameState
import com.qrakn.morpheus.game.event.GameEvent
import com.qrakn.morpheus.game.event.GameEventLogic
import com.qrakn.morpheus.game.event.impl.brackets.BracketsGameEvent
import com.qrakn.morpheus.game.parameter.GameParameter
import com.qrakn.morpheus.game.util.team.GameTeamSizeParameter
import net.frozenorb.potpvp.nametag.PotPvPNametagProvider
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack

object T4gGameEvent : GameEvent {

    private const val NAME = "TNT Tag"
    private const val PERMISSION = "practice.host.tnttag"
    private const val DESCRIPTION = "Happy blasting!"

    init {
    }

    override fun getName(): String {
        return NAME
    }


    override fun getPermission(): String {
        return PERMISSION
    }

    override fun getDescription(): String {
        return DESCRIPTION
    }

    override fun getIcon(): ItemStack {
        return ItemStack(Material.TNT)
    }

    override fun canStart(game: Game): Boolean {
        return game.players.size >= 2
    }

    override fun getLogic(game: Game): GameEventLogic {
        return T4gGameEventLogic(game)
    }

    override fun getScoreboardScores(player: Player, game: Game): List<String> {
        val toReturn = ArrayList<String>()
        val logic = game.logic as T4gGameEventLogic
        var name = NAME


        toReturn.add("&a$name Event")
        toReturn.add("");
        if (game.state == GameState.STARTING) {
            toReturn.add("&aStarts in: &f${(((game.startingAt + 500 - System.currentTimeMillis()) / 1000).toInt())}")
            toReturn.add("&aPlayers: &f${logic.getPlayersLeft()}")
        } else {
            toReturn.add("&aPlayers: &f${logic.getPlayersLeft()}&7/&f${game.players.size}")
        }

        if (game.state == GameState.RUNNING) {
            toReturn.add("&aRound: &f#${logic.currentRound}")
            if (logic.remainingTimeToBlast + 1> 0) {
                toReturn.add("&eBlast in: &f${logic.remainingTimeToBlast + 1}");
            }
        }

        return toReturn
    }

    override fun getNameTag(game: Game, player: Player, viewer: Player): String {
        if (!player.isOnline) return ChatColor.GRAY.toString() + player.name;

        if (T4gGameEventLogic.isTagged(player)) {
            return ChatColor.RED.toString();
        } else {
            if (game.spectators.contains(player)) {
                return ChatColor.GRAY.toString();
            }
        }

        return PotPvPNametagProvider.getNameColorRank(player);
    }

    override fun getListeners(): List<Listener> {
        return arrayListOf(T4gGameEventListeners())
    }

    override fun getParameters(): List<GameParameter> {
        return listOf();
    }
}