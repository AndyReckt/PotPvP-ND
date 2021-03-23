/*
 * Decompiled with CFR 0.150.
 *
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.entity.Player
 */
package net.frozenorb.potpvp.party.command;

import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.kt.command.Command;
import net.frozenorb.potpvp.kt.command.data.parameter.Param;
import net.frozenorb.potpvp.party.Party;
import net.frozenorb.potpvp.party.PartyHandler;
import net.frozenorb.potpvp.party.PartyInvite;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class PartyJoinCommand {
    private static final String NO_PASSWORD_PROVIDED="skasjkdasdjhksahjd";

    @Command(names={"party join", "p join", "t join", "team join", "f join"}, permission="")
    public static void partyJoin(Player sender, @Param(name="player") Player target, @Param(name="password", defaultValue="skasjkdasdjhksahjd") String providedPassword) {
        PartyHandler partyHandler=PotPvPND.getInstance().getPartyHandler();
        Party targetParty=partyHandler.getParty(target);
        if (partyHandler.hasParty(sender)) {
            sender.sendMessage(ChatColor.RED + "You are already in a party. You must leave your current party first.");
            return;
        }
        if (targetParty == null) {
            sender.sendMessage(ChatColor.RED + target.getName() + " is not in a party.");
            return;
        }
        PartyInvite invite=targetParty.getInvite(sender.getUniqueId());
        switch (targetParty.getAccessRestriction()) {
            case Public: {
                targetParty.join(sender);
                break;
            }
            case Invite_Only: {
                if (invite != null) {
                    targetParty.join(sender);
                    break;
                }
                sender.sendMessage(ChatColor.RED + "You don't have an invitation to this party.");
                break;
            }
            case Password: {
                if (providedPassword.equals(NO_PASSWORD_PROVIDED) && invite == null) {
                    sender.sendMessage(ChatColor.RED + "You need the password or an invitation to join this party.");
                    sender.sendMessage(ChatColor.GRAY + "To join with a password, use " + ChatColor.YELLOW + "/party join " + target.getName() + " <password>");
                    return;
                }
                String correctPassword=targetParty.getPassword();
                if (invite == null && !correctPassword.equals(providedPassword)) {
                    sender.sendMessage(ChatColor.RED + "Invalid password.");
                    break;
                }
                targetParty.join(sender);
                break;
            }
        }
    }
}

