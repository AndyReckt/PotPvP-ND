package net.frozenorb.potpvp.match.listener;

import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.MatchState;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class MatchFreezeListener implements Listener {

    @EventHandler
    public void onCountdownEnd(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        Location from = event.getFrom();
        Location to = event.getTo();

        if (from.getBlockX() == to.getBlockX() && from.getBlockZ() == to.getBlockZ()) return;

        Match match = PotPvPND.getInstance().getMatchHandler().getMatchPlaying(player);

        if (match == null || !match.getKitType().getId().equals("SUMO") || match.getState() != MatchState.COUNTDOWN) return;

        event.getPlayer().teleport(from);
    }

}
