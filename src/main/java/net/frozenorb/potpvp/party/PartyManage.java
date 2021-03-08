package net.frozenorb.potpvp.party;

import net.frozenorb.potpvp.util.CC;

import java.beans.ConstructorProperties;

public enum PartyManage {
    MEMBER_MANAGE(CC.GREEN + "Manage Members"),
    PUBLIC(CC.AQUA + "Open or Lock Party"),
    LEADER(CC.AQUA + "Make Leader"),
    KICK(CC.RED + "Kick Player");

    private final String name;

    @ConstructorProperties({"name"})
    PartyManage(final String name) {
        this.name=name;
    }

    public String getName() {
        return this.name;
    }
}