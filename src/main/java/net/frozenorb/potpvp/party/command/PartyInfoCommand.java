/*
 * Decompiled with CFR 0.150.
 *
 * Could not load the following classes:
 *  net.md_5.bungee.api.ChatColor
 *  net.md_5.bungee.api.chat.BaseComponent
 *  net.md_5.bungee.api.chat.ComponentBuilder
 *  net.md_5.bungee.api.chat.HoverEvent
 *  net.md_5.bungee.api.chat.HoverEvent$Action
 *  net.md_5.bungee.api.chat.TextComponent
 *  org.bukkit.entity.Player
 */
package net.frozenorb.potpvp.party.command;

import com.google.common.base.Joiner;
import net.frozenorb.potpvp.PotPvPLang;
import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.kt.command.Command;
import net.frozenorb.potpvp.kt.command.data.parameter.Param;
import net.frozenorb.potpvp.party.Party;
import net.frozenorb.potpvp.util.PatchedPlayerUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public final class PartyInfoCommand {
    @Command(names={"party info", "p info", "t info", "team info", "f info", "p i", "t i", "f i", "party i", "team i"}, permission="")
    public static void partyInfo(Player sender, @Param(name="player", defaultValue="self") Player target) {
        Party party=PotPvPND.getInstance().getPartyHandler().getParty(target);
        if (party == null) {
            if (sender == target) {
                sender.sendMessage(PotPvPLang.NOT_IN_PARTY);
            } else {
                sender.sendMessage(ChatColor.RED + target.getName() + " isn't in a party.");
            }
            return;
        }
        String leaderName=PotPvPND.getInstance().getUuidCache().name(party.getLeader());
        int memberCount=party.getMembers().size();
        String members=Joiner.on(", ").join(PatchedPlayerUtils.mapToNames(party.getMembers()));
        sender.sendMessage(ChatColor.GRAY + PotPvPLang.LONG_LINE);
        sender.sendMessage(ChatColor.WHITE + "Leader: " + ChatColor.AQUA + leaderName);
        sender.sendMessage(ChatColor.WHITE + "Members " + ChatColor.AQUA + "(" + memberCount + ")" + ChatColor.YELLOW + ": " + ChatColor.GRAY + members);
        switch (party.getAccessRestriction()) {
            case Public: {
                sender.sendMessage(ChatColor.WHITE + "Privacy: " + ChatColor.GREEN + "Open");
                break;
            }
            case Invite_Only: {
                sender.sendMessage(ChatColor.WHITE + "Privacy: " + ChatColor.AQUA + "Invite-Only");
                break;
            }
            case Password: {
                if (party.isLeader(sender.getUniqueId())) {
                    HoverEvent.Action showText=HoverEvent.Action.SHOW_TEXT;
                    BaseComponent[] passwordComponent=new BaseComponent[]{new TextComponent(party.getPassword())};
                    ComponentBuilder builder=new ComponentBuilder("Privacy: ").color(ChatColor.AQUA);
                    builder.append("Password Protected ").color(ChatColor.RED);
                    builder.append("[Hover for password]").color(ChatColor.GRAY);
                    builder.event(new HoverEvent(showText, passwordComponent));
                    sender.spigot().sendMessage(builder.create());
                    break;
                }
                sender.sendMessage(ChatColor.WHITE + "Privacy: " + ChatColor.AQUA + "Password Protected");
                break;
            }
        }
        sender.sendMessage(ChatColor.GRAY + PotPvPLang.LONG_LINE);
    }
}

