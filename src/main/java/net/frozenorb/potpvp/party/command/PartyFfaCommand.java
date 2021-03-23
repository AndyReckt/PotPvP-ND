/*
 * Decompiled with CFR 0.150.
 *
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 */
package net.frozenorb.potpvp.party.command;

import net.frozenorb.potpvp.PotPvPLang;
import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.kittype.menu.select.SelectKitTypeMenu;
import net.frozenorb.potpvp.kt.command.Command;
import net.frozenorb.potpvp.kt.command.data.parameter.Param;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.potpvp.match.MatchTeam;
import net.frozenorb.potpvp.party.Party;
import net.frozenorb.potpvp.party.PartyHandler;
import net.frozenorb.potpvp.util.CC;
import net.frozenorb.potpvp.validation.PotPvPValidation;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

public final class PartyFfaCommand {
    @Command(names={"party ffa", "p ffa", "t ffa", "team ffa", "f ffa"}, permission="")
    public static void partyFfa(Player sender) {
        PartyHandler partyHandler=PotPvPND.getInstance().getPartyHandler();
        Party party=partyHandler.getParty(sender);
        if (party == null) {
            sender.sendMessage(PotPvPLang.NOT_IN_PARTY);
        } else if (!party.isLeader(sender.getUniqueId())) {
            sender.sendMessage(PotPvPLang.NOT_LEADER_OF_PARTY);
        } else {
            MatchHandler matchHandler=PotPvPND.getInstance().getMatchHandler();
            if (!PotPvPValidation.canStartFfa(party, sender)) {
                return;
            }
            new SelectKitTypeMenu(kitType -> {
                sender.closeInventory();
                if (!PotPvPValidation.canStartFfa(party, sender)) {
                    return;
                }
                ArrayList<MatchTeam> teams=new ArrayList<MatchTeam>();
                for ( UUID member : party.getMembers() ) {
                    teams.add(new MatchTeam(member));
                }
                matchHandler.startMatch(teams, kitType, false, false);
            }, CC.GRAY + "Select a Kit for Party FFA").openMenu(sender);
        }
    }

    @Command(names={"party devffa", "p devffa", "t devffa", "team devffa", "f devffa"}, permission="")
    public static void partyDevFfa(Player sender, @Param(name="team size", defaultValue="1") int teamSize) {
        PartyHandler partyHandler=PotPvPND.getInstance().getPartyHandler();
        Party party=partyHandler.getParty(sender);
        if (party == null) {
            sender.sendMessage(PotPvPLang.NOT_IN_PARTY);
        } else if (!party.isLeader(sender.getUniqueId())) {
            sender.sendMessage(PotPvPLang.NOT_LEADER_OF_PARTY);
        } else {
            MatchHandler matchHandler=PotPvPND.getInstance().getMatchHandler();
            if (!PotPvPValidation.canStartFfa(party, sender)) {
                return;
            }
            new SelectKitTypeMenu(kitType -> {
                sender.closeInventory();
                if (!PotPvPValidation.canStartFfa(party, sender)) {
                    return;
                }
                ArrayList<UUID> availableMembers=new ArrayList<UUID>(party.getMembers());
                Collections.shuffle(availableMembers);
                ArrayList<MatchTeam> teams=new ArrayList<MatchTeam>();
                while (availableMembers.size() >= teamSize) {
                    ArrayList<UUID> teamMembers=new ArrayList<UUID>();
                    for ( int i=0; i < teamSize; ++i ) {
                        teamMembers.add(availableMembers.remove(0));
                    }
                    teams.add(new MatchTeam(teamMembers));
                }
                Match match=matchHandler.startMatch(teams, kitType, false, false);
                if (match != null) {
                    for ( UUID leftOut : availableMembers ) {
                        match.addSpectator(Bukkit.getPlayer(leftOut), null);
                    }
                }
            }, "Start Dev Party FFA...").openMenu(sender);
        }
    }
}

