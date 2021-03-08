/*
 * Decompiled with CFR 0.150.
 *
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package net.frozenorb.potpvp.kit.listener;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.kit.KitItems;
import net.frozenorb.potpvp.kit.menu.kits.KitsMenu;
import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.kittype.menu.select.SelectKitTypeMenu;
import net.frozenorb.potpvp.lobby.LobbyHandler;
import net.frozenorb.potpvp.util.ItemListener;
import org.bukkit.entity.Player;

public final class KitItemListener
        extends ItemListener {
    public KitItemListener() {
        this.addHandler(KitItems.OPEN_EDITOR_ITEM, player -> {
            LobbyHandler lobbyHandler=PotPvPSI.getInstance().getLobbyHandler();
            if (lobbyHandler.isInLobby(player)) {
                new SelectKitTypeMenu(kitType -> new KitsMenu(kitType).openMenu(player), "Select a kit to edit...").openMenu(player);
            }
        });
    }
}

