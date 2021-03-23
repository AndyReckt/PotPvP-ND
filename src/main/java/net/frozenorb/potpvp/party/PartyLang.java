/*
 * Decompiled with CFR 0.150.
 *
 * Could not load the following classes:
 *  net.md_5.bungee.api.ChatColor
 *  net.md_5.bungee.api.chat.BaseComponent
 *  net.md_5.bungee.api.chat.ClickEvent
 *  net.md_5.bungee.api.chat.ClickEvent$Action
 *  net.md_5.bungee.api.chat.ComponentBuilder
 *  net.md_5.bungee.api.chat.HoverEvent
 *  net.md_5.bungee.api.chat.HoverEvent$Action
 *  net.md_5.bungee.api.chat.TextComponent
 */
package net.frozenorb.potpvp.party;

import net.frozenorb.potpvp.PotPvPND;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class PartyLang {
    private static final TextComponent INVITE_PREFIX=new TextComponent("Invite > ");
    private static final TextComponent INVITED_YOU_TO_JOIN=new TextComponent(" invited you to join. ");
    private static final TextComponent ACCEPT_BUTTON=new TextComponent("[Accept]");
    private static final TextComponent INFO_BUTTON=new TextComponent("[Info]");

    public static TextComponent inviteAcceptPrompt(Party party) {
        ClickEvent.Action runCommand=ClickEvent.Action.RUN_COMMAND;
        String partyLeader=PotPvPND.getInstance().getUuidCache().name(party.getLeader());
        TextComponent acceptButton=new TextComponent(ACCEPT_BUTTON);
        TextComponent infoButton=new TextComponent(INFO_BUTTON);
        acceptButton.setClickEvent(new ClickEvent(runCommand, "/p join " + partyLeader));
        infoButton.setHoverEvent(PartyLang.hoverablePreviewTooltip(party));
        infoButton.setClickEvent(new ClickEvent(runCommand, "/p info " + partyLeader));
        TextComponent builder=new TextComponent("");
        builder.addExtra(INVITE_PREFIX);
        builder.addExtra(PartyLang.hoverablePartyName(party));
        builder.addExtra(INVITED_YOU_TO_JOIN);
        builder.addExtra(acceptButton);
        builder.addExtra(new TextComponent(" "));
        builder.addExtra(infoButton);
        return builder;
    }

    public static TextComponent hoverablePartyName(Party party) {
        TextComponent previewComponent=new TextComponent();
        String leaderName=PotPvPND.getInstance().getUuidCache().name(party.getLeader());
        if (party.getMembers().size() > 1) {
            HoverEvent hoverEvent=PartyLang.hoverablePreviewTooltip(party);
            previewComponent.setText("[" + leaderName + "'s Party]");
            previewComponent.setHoverEvent(hoverEvent);
        } else {
            previewComponent.setText(leaderName);
        }
        previewComponent.setColor(ChatColor.BLUE);
        return previewComponent;
    }

    public static HoverEvent hoverablePreviewTooltip(Party party) {
        ComponentBuilder builder=new ComponentBuilder("Members (").color(ChatColor.BLUE);
        String size="" + party.getMembers().size();
        builder.append(size).color(ChatColor.AQUA);
        builder.append("):").color(ChatColor.BLUE);
        for ( String member : PartyLang.getMemberPreviewNames(party) ) {
            builder.append("\n");
            builder.append(member);
        }
        HoverEvent.Action action=HoverEvent.Action.SHOW_TEXT;
        return new HoverEvent(action, builder.create());
    }

    private static List<String> getMemberPreviewNames(Party party) {
        ArrayList<UUID> members=new ArrayList<UUID>(party.getMembers());
        int partySize=members.size();
        ArrayList<String> displayNames=new ArrayList<String>();
        for ( int i=0; i < Math.min(partySize, 6); ++i ) {
            UUID member=members.remove(0);
            String suffix=party.isLeader(member) ? "*" : "";
            displayNames.add(ChatColor.YELLOW + PotPvPND.getInstance().getUuidCache().name(member) + suffix);
        }
        if (!members.isEmpty()) {
            displayNames.add(ChatColor.GRAY + "+ " + members.size() + " more");
        }
        return displayNames;
    }

    private PartyLang() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    static {
        INVITE_PREFIX.setColor(ChatColor.AQUA);
        INVITE_PREFIX.setBold(Boolean.valueOf(true));
        INVITED_YOU_TO_JOIN.setColor(ChatColor.YELLOW);
        HoverEvent.Action showText=HoverEvent.Action.SHOW_TEXT;
        BaseComponent[] acceptTooltip=new ComponentBuilder("Click to join party").color(ChatColor.GREEN).create();
        ACCEPT_BUTTON.setColor(ChatColor.GREEN);
        ACCEPT_BUTTON.setHoverEvent(new HoverEvent(showText, acceptTooltip));
        INFO_BUTTON.setColor(ChatColor.AQUA);
    }
}

