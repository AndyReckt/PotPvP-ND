/*
 * Decompiled with CFR 0.150.
 *
 * Could not load the following classes:
 *  org.bukkit.event.Event
 */
package net.frozenorb.potpvp.party.event;

import com.google.common.base.Preconditions;
import net.frozenorb.potpvp.party.Party;
import org.bukkit.event.Event;

abstract class PartyEvent
        extends Event {
    private final Party party;

    PartyEvent(Party party) {
        this.party=Preconditions.checkNotNull(party, "party");
    }

    public Party getParty() {
        return this.party;
    }
}

