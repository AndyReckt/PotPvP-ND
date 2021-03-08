/*
 * Decompiled with CFR 0.150.
 */
package net.frozenorb.potpvp.party;

import com.google.common.base.Preconditions;

import java.time.Instant;
import java.util.UUID;

public final class PartyInvite {
    private final Party party;
    private final UUID target;
    private final Instant timeSent;

    PartyInvite(Party party, UUID target) {
        this.party=Preconditions.checkNotNull(party, "party");
        this.target=Preconditions.checkNotNull(target, "target");
        this.timeSent=Instant.now();
    }

    public Party getParty() {
        return this.party;
    }

    public UUID getTarget() {
        return this.target;
    }

    public Instant getTimeSent() {
        return this.timeSent;
    }
}

