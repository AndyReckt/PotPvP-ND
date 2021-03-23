package net.frozenorb.potpvp.party.listener;

import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.match.event.MatchSpectatorLeaveEvent;
import net.frozenorb.potpvp.party.Party;
import net.frozenorb.potpvp.party.PartyHandler;
import net.frozenorb.potpvp.party.PartyInvite;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public final class PartyLeaveListener
        implements Listener {
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player=event.getPlayer();
        UUID playerUuid=player.getUniqueId();
        for ( Party party : PotPvPND.getInstance().getPartyHandler().getParties() ) {
            PartyInvite invite;
            if (party.isMember(playerUuid)) {
                party.leave(player);
            }
            if ((invite=party.getInvite(playerUuid)) == null) continue;
            party.revokeInvite(invite);
        }
    }

    @EventHandler
    public void onMatchSpectatorLeave(MatchSpectatorLeaveEvent event) {
        PartyHandler partyHandler=PotPvPND.getInstance().getPartyHandler();
        Party party=partyHandler.getParty(event.getSpectator());
        if (party != null) {
            party.leave(event.getSpectator());
        }
    }
}

