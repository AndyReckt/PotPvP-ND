/*
 * Decompiled with CFR 0.150.
 *
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.event.HandlerList
 */
package net.frozenorb.potpvp.party.event;

import com.google.common.base.Preconditions;
import net.frozenorb.potpvp.party.Party;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public final class PartyMemberKickEvent
        extends PartyEvent {
    private static final HandlerList handlerList=new HandlerList();
    private final Player member;

    public PartyMemberKickEvent(Player member, Party party) {
        super(party);
        this.member=Preconditions.checkNotNull(member, "member");
    }

    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public Player getMember() {
        return this.member;
    }
}

