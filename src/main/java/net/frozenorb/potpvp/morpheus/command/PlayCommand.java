package net.frozenorb.potpvp.morpheus.command;

import com.qrakn.morpheus.game.Game;
import com.qrakn.morpheus.game.GameQueue;
import com.qrakn.morpheus.game.GameState;
import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.kt.command.Command;
import net.frozenorb.potpvp.lobby.LobbyHandler;
import net.frozenorb.potpvp.party.PartyHandler;
import net.frozenorb.potpvp.queue.QueueHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PlayCommand {

    @Command(names = {"play"}, permission = "")
    public static void host(Player sender) {
        LobbyHandler lobbyHandler = PotPvPND.getInstance().getLobbyHandler();
        if (!lobbyHandler.isInLobby(sender) || lobbyHandler.isInSpectatorMode(sender)) return;

        QueueHandler queueHandler = PotPvPND.getInstance().getQueueHandler();
        if (queueHandler.isQueued(sender.getUniqueId())) return;

        PartyHandler partyHandler = PotPvPND.getInstance().getPartyHandler();
        if (partyHandler.hasParty(sender)) {
            sender.sendMessage(ChatColor.RED + "You must leave your party to join the event!");
            return;
        }

        for (Game game : GameQueue.INSTANCE.getCurrentGames()) {
            if (game.getState() == GameState.STARTING) {
                if (game.getMaxPlayers() > 0 && game.getPlayers().size() >= game.getMaxPlayers()) {
                    sender.sendMessage(ChatColor.RED + "This event is currently full! Sorry!");
                    return;
                }
                game.add(sender);
            } else {
                game.addSpectator(sender);
            }
        }
    }
}
