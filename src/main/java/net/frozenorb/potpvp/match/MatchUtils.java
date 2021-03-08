package net.frozenorb.potpvp.match;

import lombok.experimental.UtilityClass;
import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.follow.FollowHandler;
import net.frozenorb.potpvp.lobby.LobbyItems;
import net.frozenorb.potpvp.party.PartyHandler;
import net.frozenorb.potpvp.setting.Setting;
import net.frozenorb.potpvp.setting.SettingHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

@UtilityClass
public final class MatchUtils {

    public static void resetInventory(Player player) {
        SettingHandler settingHandler = PotPvPSI.getInstance().getSettingHandler();
        FollowHandler followHandler = PotPvPSI.getInstance().getFollowHandler();
        PartyHandler partyHandler = PotPvPSI.getInstance().getPartyHandler();
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();

        Match match = matchHandler.getMatchSpectating(player);

        // because we lookup their match with getMatchSpectating this will also
        // return for players fighting in matches
        if (match == null) {
            return;
        }

        PlayerInventory inventory = player.getInventory();

        inventory.clear();
        inventory.setArmorContents(null);

        // don't give players who die (and cause the match to end)
        // a fire item, they'll be sent back to the lobby in a few seconds anyway
        if (match.getState() == MatchState.ENDING) {
            Bukkit.getScheduler().runTaskLater(PotPvPSI.getInstance(), player::updateInventory, 1L);
            return;
        }
        // if they've been on any team or are staff they'll be able to
        // use this item on at least 1 player. if they can't use it all
        // we just don't give it to them (UX purposes)
        boolean bypassViewInventories = player.hasPermission("potpvp.inventory.all");

        boolean canViewInventories = match.getTeams()
            .stream()
            .map(MatchTeam::getAllMembers)
            .anyMatch(members -> members.contains(player.getUniqueId()));

        // fill inventory with spectator items
        if (canViewInventories || bypassViewInventories) {
            inventory.setItem(0, SpectatorItems.VIEW_INVENTORY_ITEM);
        }
        // Set staff mode items if player is vanished.
        if (settingHandler.getSetting(player, Setting.VIEW_OTHER_SPECTATORS)) {
            inventory.setItem(1, SpectatorItems.HIDE_SPECTATORS_ITEM);
        } else {
            inventory.setItem(1, SpectatorItems.SHOW_SPECTATORS_ITEM);
        }
        if (partyHandler.hasParty(player)) {
            inventory.setItem(8, SpectatorItems.LEAVE_PARTY_ITEM);
        } else {
            inventory.setItem(8, SpectatorItems.RETURN_TO_LOBBY_ITEM);
            inventory.setItem(3, LobbyItems.SPECTATE_RANDOM_ITEM);
            inventory.setItem(2, LobbyItems.SPECTATE_MENU_ITEM);
        }

        Bukkit.getScheduler().runTaskLater(PotPvPSI.getInstance(), player::updateInventory, 1L);
    }

}