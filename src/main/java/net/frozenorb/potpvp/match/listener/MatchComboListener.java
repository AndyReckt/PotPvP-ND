package net.frozenorb.potpvp.match.listener;

import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.event.MatchStartEvent;
import net.hylist.knockback.KnockbackProfile;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.spigotmc.SpigotConfig;

import java.util.Objects;
import java.util.UUID;

public class MatchComboListener implements Listener {

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onStart(MatchStartEvent event) {
        Match match = event.getMatch();
        int noDamageTicks = match.getKitType().getId().contains("Combo") ? 2 : 20;
        match.getTeams().forEach(team -> team.getAliveMembers().stream().map(Bukkit::getPlayer).filter(Objects::nonNull).forEach(p -> p.setMaximumNoDamageTicks(noDamageTicks)));
        for ( UUID uuid : match.getAllPlayers() ) {
            Player player = Bukkit.getPlayer(uuid);
            ((CraftPlayer)player).getHandle().setKbProfile(SpigotConfig.getKbProfileByName("Combo"));
        }
    }
}
