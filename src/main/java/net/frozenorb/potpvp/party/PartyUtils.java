package net.frozenorb.potpvp.party;

import com.google.common.collect.ImmutableList;
import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.kittype.menu.select.SelectKitTypeMenu;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.MatchTeam;
import net.frozenorb.potpvp.party.menu.oddmanout.OddManOutMenu;
import net.frozenorb.potpvp.util.CC;
import net.frozenorb.potpvp.validation.PotPvPValidation;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.UUID;

public final class PartyUtils {
    public static void startTeamSplit(Party party, Player initiator) {
        if (!PotPvPValidation.canStartTeamSplit(party, initiator)) {
            return;
        }
        new SelectKitTypeMenu(kitType -> {
            initiator.closeInventory();
            if (party.getMembers().size() % 2 == 0) {
                PartyUtils.startTeamSplit(party, initiator, kitType, false);
            } else {
                new OddManOutMenu(oddManOut -> {
                    initiator.closeInventory();
                    PartyUtils.startTeamSplit(party, initiator, kitType, oddManOut);
                }).openMenu(initiator);
            }
        }, CC.GRAY + "Select a Kit for Split Match").openMenu(initiator);
    }

    public static void startTeamSplit(Party party, Player initiator, KitType kitType, boolean oddManOut) {
        Match match;
        if (!PotPvPValidation.canStartTeamSplit(party, initiator)) {
            return;
        }
        ArrayList<UUID> members=new ArrayList<UUID>(party.getMembers());
        Collections.shuffle(members);
        HashSet<UUID> team1=new HashSet<UUID>();
        HashSet<UUID> team2=new HashSet<UUID>();
        Player spectator=null;
        while (members.size() >= 2) {
            team1.add(members.remove(0));
            team2.add(members.remove(0));
        }
        if (!members.isEmpty()) {
            if (oddManOut) {
                spectator=Bukkit.getPlayer(members.remove(0));
                party.message(ChatColor.AQUA + spectator.getName() + CC.YELLOW + " was selected as the odd-man out.");
            } else {
                team1.add(members.remove(0));
            }
        }
        if ((match=PotPvPSI.getInstance().getMatchHandler().startMatch(ImmutableList.of(new MatchTeam(team1), new MatchTeam(team2)), kitType, false, false)) == null) {
            initiator.sendMessage(ChatColor.RED + "Failed to start team split.");
            return;
        }
        if (spectator != null) {
            match.addSpectator(spectator, null);
        }
    }

    private PartyUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}

