/*
 * Decompiled with CFR 0.150.
 */
package net.frozenorb.potpvp.queue;

import com.google.common.base.Preconditions;
import net.frozenorb.potpvp.party.Party;

import java.util.Set;
import java.util.UUID;

public final class PartyMatchQueueEntry
        extends MatchQueueEntry {
    private final Party party;

    PartyMatchQueueEntry(MatchQueue queue, Party party) {
        super(queue);
        this.party=Preconditions.checkNotNull(party, "party");
    }

    @Override
    public Set<UUID> getMembers() {
        return this.party.getMembers();
    }

    public Party getParty() {
        return this.party;
    }
}

