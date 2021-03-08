/*
 * Decompiled with CFR 0.150.
 *
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.scheduler.BukkitRunnable
 */
package net.frozenorb.potpvp.morpheus;

import com.qrakn.morpheus.game.Game;
import com.qrakn.morpheus.game.GameQueue;
import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.lobby.LobbyHandler;
import net.frozenorb.potpvp.lobby.LobbyUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class EventTask
        extends BukkitRunnable {
    public void run() {
        for ( Player player : Bukkit.getOnlinePlayers() ) {
            LobbyHandler handler=PotPvPSI.getInstance().getLobbyHandler();
            if (handler.isInLobby(player)) {
                List<Game> games=GameQueue.INSTANCE.getCurrentGames();
                Game game=GameQueue.INSTANCE.getCurrentGame(player);
                if (games.isEmpty()) {
                    if (!player.getInventory().contains(Material.EMERALD)) continue;
                    player.getInventory().remove(Material.EMERALD);
                    continue;
                }
                if (game != null || player.getInventory().contains(EventItems.getEventItem()) || PotPvPSI.getInstance().getPartyHandler().hasParty(player))
                    continue;
                LobbyUtils.resetInventory(player);
                continue;
            }
            Game game=GameQueue.INSTANCE.getCurrentGame(player);
            if (game == null || !game.getPlayers().contains(player) || !player.getInventory().contains(EventItems.getEventItem()))
                continue;
            player.getInventory().remove(Material.EMERALD);
        }
    }
}

