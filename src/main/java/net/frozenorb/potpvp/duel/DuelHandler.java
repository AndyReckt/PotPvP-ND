package net.frozenorb.potpvp.duel;

import com.google.common.collect.Lists;
import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.duel.listener.DuelListener;
import net.frozenorb.potpvp.lobby.LobbyUtils;
import net.frozenorb.potpvp.party.Party;
import net.frozenorb.potpvp.util.TaskUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class DuelHandler {

    public static final int DUEL_INVITE_TIMEOUT_SECONDS = 30;

    // this does mean lookups are O(n), but unlike matches or parties
    // there isn't enough volume + frequency to become an issue
    private Set<DuelInvite> activeInvites = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public DuelHandler() {
        Bukkit.getPluginManager().registerEvents(new DuelListener(), PotPvPND.getInstance());
        Bukkit.getScheduler().runTaskTimerAsynchronously(PotPvPND.getInstance(), () -> {
            activeInvites.forEach(duelInvite -> {
                if (duelInvite instanceof PlayerDuelInvite && duelInvite.isExpired()) {
                    TaskUtil.run(() -> {
                        Player player = Bukkit.getPlayer(((PlayerDuelInvite) duelInvite).getTarget());
                        LobbyUtils.resetInventory(player);
                    });
                }
            });

            activeInvites.removeIf(DuelInvite::isExpired);
        }, 20, 20);
    }

    public void insertInvite(DuelInvite invite) {
        activeInvites.add(invite);

        if (invite instanceof PlayerDuelInvite) {
            PlayerDuelInvite duelInvite = (PlayerDuelInvite) invite;
            TaskUtil.runLater(() -> LobbyUtils.resetInventory(Bukkit.getPlayer(duelInvite.getTarget())), 3L);
        }
    }

    public void removeInvite(DuelInvite invite) {
        activeInvites.remove(invite);
    }

    public void removeInvitesTo(Player player) {
        activeInvites.removeIf(i ->
            i instanceof PlayerDuelInvite &&
                ((PlayerDuelInvite) i).getTarget().equals(player.getUniqueId())
        );
    }

    public void removeInvitesFrom(Player player) {
        activeInvites.removeIf(i ->
            i instanceof PlayerDuelInvite &&
                ((PlayerDuelInvite) i).getSender().equals(player.getUniqueId())
        );
    }

    public void removeInvitesTo(Party party) {
        activeInvites.removeIf(i ->
            i instanceof PartyDuelInvite &&
                ((PartyDuelInvite) i).getTarget() == party
        );
    }

    public void removeInvitesFrom(Party party) {
        activeInvites.removeIf(i ->
            i instanceof PartyDuelInvite &&
                ((PartyDuelInvite) i).getSender() == party
        );
    }

    public PartyDuelInvite findInvite(Party sender, Party target) {
        for (DuelInvite invite : activeInvites) {
            if (invite instanceof PartyDuelInvite) {
                PartyDuelInvite partyInvite = (PartyDuelInvite) invite;

                if (partyInvite.getSender() == sender && partyInvite.getTarget() == target) {
                    return partyInvite;
                }
            }
        }

        return null;
    }


    public List<PlayerDuelInvite> getInvitesTo(Player player) {
        List<PlayerDuelInvite> invites = Lists.newArrayList();
        for (DuelInvite invite : activeInvites) {
            if (invite instanceof PlayerDuelInvite) {
                PlayerDuelInvite playerInvite = (PlayerDuelInvite) invite;
                if (playerInvite.getTarget().equals(player.getUniqueId())) {
                    invites.add(playerInvite);
                }
            }
        }
        return invites;
    }

    public PlayerDuelInvite findInvite(Player sender, Player target) {
        for (DuelInvite invite : activeInvites) {
            if (invite instanceof PlayerDuelInvite) {
                PlayerDuelInvite playerInvite = (PlayerDuelInvite) invite;

                if (playerInvite.getSender().equals(sender.getUniqueId()) && playerInvite.getTarget().equals(target.getUniqueId())) {
                    return playerInvite;
                }
            }
        }

        return null;
    }

}