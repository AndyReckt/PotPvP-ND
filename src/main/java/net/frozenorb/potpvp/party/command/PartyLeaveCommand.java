/*
 * Decompiled with CFR 0.150.
 *
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package net.frozenorb.potpvp.party.command;

import net.frozenorb.potpvp.PotPvPLang;
import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.kt.command.Command;
import net.frozenorb.potpvp.party.Party;
import org.bukkit.entity.Player;

public final class PartyLeaveCommand {
    @Command(names={"party leave", "p leave", "t leave", "team leave", "leave", "f leave"}, permission="")
    public static void partyLeave(Player sender) {
        Party party=PotPvPND.getInstance().getPartyHandler().getParty(sender);
        if (party == null) {
            sender.sendMessage(PotPvPLang.NOT_IN_PARTY);
        } else {
            party.leave(sender);
        }
    }
}

