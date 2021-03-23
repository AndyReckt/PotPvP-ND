/*
 * Decompiled with CFR 0.150.
 *
 * Could not load the following classes:
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.AsyncPlayerPreLoginEvent
 *  org.bukkit.event.player.PlayerQuitEvent
 */
package net.frozenorb.potpvp.kit.listener;

import net.frozenorb.potpvp.PotPvPND;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class KitLoadListener
        implements Listener {
    @EventHandler(priority=EventPriority.LOWEST)
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        PotPvPND.getInstance().getKitHandler().loadKits(event.getUniqueId());
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        PotPvPND.getInstance().getKitHandler().unloadKits(event.getPlayer());
    }
}

