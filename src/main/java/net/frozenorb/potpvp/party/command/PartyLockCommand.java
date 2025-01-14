package net.frozenorb.potpvp.party.command;

import net.frozenorb.potpvp.PotPvPLang;
import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.kt.command.Command;
import net.frozenorb.potpvp.party.Party;
import net.frozenorb.potpvp.party.PartyAccessRestriction;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class PartyLockCommand {
    @Command(names={"party lock", "p lock", "t lock", "team lock", "f lock"}, permission="practice.donator")
    public static void partyLock(Player sender) {
        Party party=PotPvPND.getInstance().getPartyHandler().getParty(sender);
        if (party == null) {
            sender.sendMessage(PotPvPLang.NOT_IN_PARTY);
        } else if (!party.isLeader(sender.getUniqueId())) {
            sender.sendMessage(PotPvPLang.NOT_LEADER_OF_PARTY);
        } else if (party.getAccessRestriction() == PartyAccessRestriction.Invite_Only) {
            sender.sendMessage(ChatColor.RED + "Your party is already locked.");
        } else {
            party.setAccessRestriction(PartyAccessRestriction.Invite_Only);
            sender.sendMessage(ChatColor.YELLOW + "Your party is now " + ChatColor.RED + "locked" + ChatColor.YELLOW + ".");
        }
    }
}

