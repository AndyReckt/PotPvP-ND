/*
 * Decompiled with CFR 0.150.
 *
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.AsyncPlayerChatEvent
 */
package net.frozenorb.potpvp.party.listener;

import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.party.Party;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class PartyChatListener
        implements Listener {
    private final Map<UUID, Long> canUsePartyChat=new ConcurrentHashMap<UUID, Long>();

    @EventHandler(ignoreCancelled=true)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        if (!event.getMessage().startsWith("@")) {
            return;
        }
        event.setCancelled(true);
        Player player=event.getPlayer();
        String message=event.getMessage().substring(1).trim();
        Party party=PotPvPND.getInstance().getPartyHandler().getParty(player);
        if (party == null) {
            player.sendMessage(ChatColor.RED + "You aren't in a party!");
            return;
        }
        if (this.canUsePartyChat.getOrDefault(player.getUniqueId(), 0L) > System.currentTimeMillis()) {
            player.sendMessage(ChatColor.RED + "Wait a bit before sending another message.");
            return;
        }
        ChatColor prefixColor=party.isLeader(player.getUniqueId()) ? PotPvPND.getInstance().getDominantColor() : ChatColor.LIGHT_PURPLE;
        party.message(prefixColor.toString() + ChatColor.BOLD + "[P] " + player.getName() + ": " + ChatColor.LIGHT_PURPLE + message);
        this.canUsePartyChat.put(player.getUniqueId(), System.currentTimeMillis() + 2000L);
        PotPvPND.getInstance().getLogger().info("[Party Chat] " + player.getName() + ": " + message);
    }
}

