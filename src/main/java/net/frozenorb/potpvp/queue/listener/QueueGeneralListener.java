package net.frozenorb.potpvp.queue.listener;

import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.match.MatchTeam;
import net.frozenorb.potpvp.match.event.MatchCountdownStartEvent;
import net.frozenorb.potpvp.match.event.MatchSpectatorJoinEvent;
import net.frozenorb.potpvp.party.Party;
import net.frozenorb.potpvp.party.PartyHandler;
import net.frozenorb.potpvp.party.event.*;
import net.frozenorb.potpvp.queue.QueueHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public final class QueueGeneralListener
        implements Listener {
    private final QueueHandler queueHandler;

    public QueueGeneralListener(QueueHandler queueHandler) {
        this.queueHandler=queueHandler;
    }

    @EventHandler
    public void onPartyDisband(PartyDisbandEvent event) {
        this.queueHandler.leaveQueue(event.getParty(), true);
    }

    @EventHandler
    public void onPartyCreate(PartyCreateEvent event) {
        UUID leaderUuid=event.getParty().getLeader();
        Player leaderPlayer=Bukkit.getPlayer(leaderUuid);
        this.queueHandler.leaveQueue(leaderPlayer, false);
    }

    @EventHandler
    public void onPartyMemberJoin(PartyMemberJoinEvent event) {
        this.queueHandler.leaveQueue(event.getMember(), false);
        this.leaveQueue(event.getParty(), event.getMember(), "joined");
    }

    @EventHandler
    public void onPartyMemberKick(PartyMemberKickEvent event) {
        this.leaveQueue(event.getParty(), event.getMember(), "was kicked");
    }

    @EventHandler
    public void onPartyMemberLeave(PartyMemberLeaveEvent event) {
        this.leaveQueue(event.getParty(), event.getMember(), "left");
    }

    private void leaveQueue(Party party, Player member, String action) {
        if (this.queueHandler.leaveQueue(party, true)) {
            party.message(ChatColor.RED + "Your party has been removed from the queue because " + member.getName() + " " + action + ".");
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.queueHandler.leaveQueue(event.getPlayer(), true);
    }

    @EventHandler
    public void onMatchSpectatorJoin(MatchSpectatorJoinEvent event) {
        this.queueHandler.leaveQueue(event.getSpectator(), true);
    }

    @EventHandler
    public void onMatchCountdownStart(MatchCountdownStartEvent event) {
        PartyHandler partyHandler=PotPvPND.getInstance().getPartyHandler();
        for ( MatchTeam team : event.getMatch().getTeams() ) {
            for ( UUID member : team.getAllMembers() ) {
                Player memberBukkit=Bukkit.getPlayer(member);
                Party memberParty=partyHandler.getParty(memberBukkit);
                this.queueHandler.leaveQueue(memberBukkit, true);
                if (memberParty == null || !memberParty.isLeader(member)) continue;
                this.queueHandler.leaveQueue(memberParty, true);
            }
        }
    }
}

