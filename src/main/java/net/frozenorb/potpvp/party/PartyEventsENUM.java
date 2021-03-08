package net.frozenorb.potpvp.party;

import java.beans.ConstructorProperties;

public enum PartyEventsENUM {
    PartySplit("Split Party"),
    PartyFFA("Party FFA"),
    PARTYUnranked("Party 2v2 Unranked"),
    PARTYRanked("Party 2v2 Ranked");
    private final String name;

    @ConstructorProperties({"name"})
    PartyEventsENUM(final String name) {
        this.name=name;
    }

    public String getName() {
        return this.name;
    }
}
