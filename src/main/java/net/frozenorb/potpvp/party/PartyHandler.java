/*
 * Decompiled with CFR 0.150.
 *
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Listener
 *  org.bukkit.plugin.Plugin
 */
package net.frozenorb.potpvp.party;

import com.google.common.collect.ImmutableSet;
import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.party.listener.PartyChatListener;
import net.frozenorb.potpvp.party.listener.PartyItemListener;
import net.frozenorb.potpvp.party.listener.PartyLeaveListener;
import net.frozenorb.potpvp.util.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class PartyHandler {
    static final int INVITE_EXPIRATION_SECONDS=30;
    private final Set<Party> parties=Collections.newSetFromMap(new ConcurrentHashMap());
    private final Map<UUID, Party> playerPartyCache=new ConcurrentHashMap<UUID, Party>();

    public PartyHandler() {
        Bukkit.getPluginManager().registerEvents(new PartyChatListener(), PotPvPND.getInstance());
        Bukkit.getPluginManager().registerEvents(new PartyItemListener(this), PotPvPND.getInstance());
        Bukkit.getPluginManager().registerEvents(new PartyLeaveListener(), PotPvPND.getInstance());
    }

    public Set<Party> getParties() {
        return ImmutableSet.copyOf(this.parties);
    }

    public boolean hasParty(Player player) {
        return this.playerPartyCache.containsKey(player.getUniqueId());
    }

    public Party getParty(Player player) {
        return this.playerPartyCache.get(player.getUniqueId());
    }

    public Party getParty(UUID uuid) {
        return this.playerPartyCache.get(uuid);
    }

    public Party getOrCreateParty(Player player) {
        Party party=this.getParty(player);
        if (party == null) {
            party=new Party(player.getUniqueId());
            this.parties.add(party);
            InventoryUtils.resetInventoryDelayed(player);
        }
        return party;
    }

    void unregisterParty(Party party) {
        this.parties.remove(party);
    }

    public void updatePartyCache(UUID playerUuid, Party party) {
        if (party != null) {
            this.playerPartyCache.put(playerUuid, party);
        } else {
            this.playerPartyCache.remove(playerUuid);
        }
    }
}

