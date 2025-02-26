package net.frozenorb.potpvp.rematch.listener;

import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.match.event.MatchTerminateEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public final class RematchGeneralListener implements Listener {

    @EventHandler
    public void onMatchTerminate(MatchTerminateEvent event) {
        PotPvPND.getInstance().getRematchHandler().registerRematches(event.getMatch());
    }

}