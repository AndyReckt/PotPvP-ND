package net.frozenorb.potpvp.setting.listener;

import net.frozenorb.potpvp.PotPvPND;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class SettingLoadListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST) // LOWEST runs first
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        PotPvPND.getInstance().getSettingHandler().loadSettings(event.getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR) // MONITOR runs last
    public void onPlayerQuit(PlayerQuitEvent event) {
        PotPvPND.getInstance().getSettingHandler().unloadSettings(event.getPlayer());
    }

}