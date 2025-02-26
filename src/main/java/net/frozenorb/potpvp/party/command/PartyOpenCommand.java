/*
 * Decompiled with CFR 0.150.
 *
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.entity.Player
 */
package net.frozenorb.potpvp.party.command;

import net.frozenorb.potpvp.PotPvPLang;
import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.kt.command.Command;
import net.frozenorb.potpvp.party.Party;
import net.frozenorb.potpvp.party.PartyAccessRestriction;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class PartyOpenCommand {
    @Command(names={"party open", "p open", "t open", "team open", "f open", "party unlock", "p unlock", "t unlock", "team unlock", "f unlock"}, permission="practice.donator")
    public static void partyOpen(Player sender) {
        Party party=PotPvPND.getInstance().getPartyHandler().getParty(sender);
        if (party == null) {
            sender.sendMessage(PotPvPLang.NOT_IN_PARTY);
        } else if (!party.isLeader(sender.getUniqueId())) {
            sender.sendMessage(PotPvPLang.NOT_LEADER_OF_PARTY);
        } else if (party.getAccessRestriction() == PartyAccessRestriction.Public) {
            sender.sendMessage(ChatColor.RED + "Your party is already open.");
        } else {
            party.setAccessRestriction(PartyAccessRestriction.Public);
            sender.sendMessage(ChatColor.YELLOW + "Your party is now " + ChatColor.GREEN + "open" + ChatColor.YELLOW + ".");
        }
    }
}

