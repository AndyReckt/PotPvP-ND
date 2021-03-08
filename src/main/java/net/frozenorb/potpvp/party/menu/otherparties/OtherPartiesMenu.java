/*
 * Decompiled with CFR 0.150.
 *
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 */
package net.frozenorb.potpvp.party.menu.otherparties;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.kt.menu.Button;
import net.frozenorb.potpvp.kt.menu.pagination.PaginatedMenu;
import net.frozenorb.potpvp.lobby.LobbyHandler;
import net.frozenorb.potpvp.party.Party;
import net.frozenorb.potpvp.party.PartyHandler;
import net.frozenorb.potpvp.setting.Setting;
import net.frozenorb.potpvp.setting.SettingHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public final class OtherPartiesMenu
        extends PaginatedMenu {
    public OtherPartiesMenu() {
        this.setPlaceholder(true);
        this.setAutoUpdate(true);
    }

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Other parties";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        SettingHandler settingHandler=PotPvPSI.getInstance().getSettingHandler();
        PartyHandler partyHandler=PotPvPSI.getInstance().getPartyHandler();
        LobbyHandler lobbyHandler=PotPvPSI.getInstance().getLobbyHandler();
        HashMap<Integer, Button> buttons=new HashMap<Integer, Button>();
        ArrayList<Party> parties=new ArrayList<Party>(partyHandler.getParties());
        int index=0;
        parties.sort(Comparator.comparing(p -> p.getMembers().size()));
        for ( Party party : parties ) {
            if (party.isMember(player.getUniqueId()) || !lobbyHandler.isInLobby(Bukkit.getPlayer(party.getLeader())) || !settingHandler.getSetting(Bukkit.getPlayer(party.getLeader()), Setting.RECEIVE_DUELS))
                continue;
            buttons.put(index++, new OtherPartyButton(party));
        }
        return buttons;
    }

    @Override
    public int size(Map<Integer, ? extends Button> buttons) {
        return 54;
    }

    @Override
    public int getMaxItemsPerPage(Player player) {
        return 45;
    }
}

