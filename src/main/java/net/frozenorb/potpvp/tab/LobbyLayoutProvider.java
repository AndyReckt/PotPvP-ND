package net.frozenorb.potpvp.tab;

import com.google.common.collect.Sets;
import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.elo.EloHandler;
import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.kt.tab.TabLayout;
import net.frozenorb.potpvp.party.Party;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;

final class LobbyLayoutProvider implements BiConsumer<Player, TabLayout> {

    LobbyLayoutProvider() {
    }

    @Override
    public void accept(Player player, TabLayout tabLayout) {
        Party party=PotPvPSI.getInstance().getPartyHandler().getParty(player);
        EloHandler eloHandler=PotPvPSI.getInstance().getEloHandler();
        tabLayout.set(1, 3, PotPvPSI.getInstance().getDominantColor().toString() + ChatColor.BOLD + "Your Rankings");
        int x=0;
        int y=4;
        for ( KitType kitType : KitType.getAllTypes() ) {
            if (kitType.isHidden() || !kitType.isSupportsRanked()) continue;
            tabLayout.set(x++, y, ChatColor.translateAlternateColorCodes('&', "&b" + kitType.getDisplayName() + " &7- &b" + eloHandler.getElo(player, kitType)));
            if (x != 3) continue;
            x=0;
            ++y;
        }
        if (party == null) {
            return;
        }
        tabLayout.set(1, 8, ChatColor.BLUE.toString() + ChatColor.BOLD + "Your Party");
        x=0;
        y=9;
        for ( UUID member : this.getOrderedMembers(player, party) ) {
            int ping=PotPvPLayoutProvider.getPingOrDefault(member);
            String suffix=member == party.getLeader() ? ChatColor.GRAY + "*" : "";
            String displayName=ChatColor.BLUE + PotPvPSI.getInstance().getUuidCache().name(member) + suffix;
            tabLayout.set(x++, y, displayName, ping);
            if (x == 3 && y == 20) break;
            if (x != 3) continue;
            x=0;
            ++y;
        }
    }

    private Set<UUID> getOrderedMembers(Player viewer, Party party) {
        Set<UUID> orderedMembers=Sets.newSetFromMap(new LinkedHashMap<>());
        UUID leader=party.getLeader();
        orderedMembers.add(viewer.getUniqueId());
        if (viewer.getUniqueId() != leader) {
            orderedMembers.add(leader);
        }
        for ( UUID member : party.getMembers() ) {
            if (member == leader || member == viewer.getUniqueId()) continue;
            orderedMembers.add(member);
        }
        return orderedMembers;
    }
}
