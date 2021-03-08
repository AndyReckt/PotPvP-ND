/*
 * Decompiled with CFR 0.150.
 *
 * Could not load the following classes:
 *  org.bukkit.event.HandlerList
 */
package net.frozenorb.potpvp.party.event;

import net.frozenorb.potpvp.party.Party;
import org.bukkit.event.HandlerList;

public final class PartyDisbandEvent
        extends PartyEvent {
    private static final HandlerList handlerList=new HandlerList();

    public PartyDisbandEvent(Party party) {
        super(party);
    }

    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}

