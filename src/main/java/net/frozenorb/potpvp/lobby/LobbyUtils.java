package net.frozenorb.potpvp.lobby;

import lombok.experimental.UtilityClass;
import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.duel.DuelHandler;
import net.frozenorb.potpvp.duel.PlayerDuelInvite;
import net.frozenorb.potpvp.elo.EloHandler;
import net.frozenorb.potpvp.follow.FollowHandler;
import net.frozenorb.potpvp.kit.KitItems;
import net.frozenorb.potpvp.kit.menu.editkit.EditKitMenu;
import net.frozenorb.potpvp.kt.menu.Menu;
import net.frozenorb.potpvp.kt.util.ItemUtils;
import net.frozenorb.potpvp.morpheus.EventItems;
import net.frozenorb.potpvp.party.Party;
import net.frozenorb.potpvp.party.PartyHandler;
import net.frozenorb.potpvp.party.PartyItems;
import net.frozenorb.potpvp.queue.QueueHandler;
import net.frozenorb.potpvp.queue.QueueItems;
import net.frozenorb.potpvp.rematch.RematchData;
import net.frozenorb.potpvp.rematch.RematchHandler;
import net.frozenorb.potpvp.rematch.RematchItems;
import net.frozenorb.potpvp.util.CC;
import net.frozenorb.potpvp.validation.PotPvPValidation;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.List;

import static org.bukkit.ChatColor.GREEN;

@UtilityClass
public final class LobbyUtils {

    public static void resetInventory(Player player) {
        // prevents players with the kit editor from having their
        // inventory updated (kit items go into their inventory)
        // also, admins in GM don't get invs updated (to prevent annoying those editing kits)
        if (Menu.getCurrentlyOpenedMenus().get(player.getName()) instanceof EditKitMenu || player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        // Stop if player is in a match
        if (PotPvPND.getInstance().getMatchHandler().isPlayingOrSpectatingMatch(player)) return;

        PartyHandler partyHandler = PotPvPND.getInstance().getPartyHandler();
        PlayerInventory inventory = player.getInventory();

        inventory.clear();
        inventory.setArmorContents(null);

        if (PotPvPValidation.isInGame(player)) return;
        if (partyHandler.hasParty(player)) {
            renderPartyItems(player, inventory, partyHandler.getParty(player));
        } else {
            renderSoloItems(player, inventory);
        }

        Bukkit.getScheduler().runTaskLater(PotPvPND.getInstance(), player::updateInventory, 1L);
    }

    public static String getDivision(final Player player) {
        EloHandler eloHandler = PotPvPND.getInstance().getEloHandler();
        int globalElo = eloHandler.getGlobalElo(player.getUniqueId());
        String division = "";
        if (globalElo <= 1000) {
            division = CC.GRAY + "Silver V";
        }
        if (globalElo >= 1000) {
            division = CC.GRAY + "Silver IV";
        }
        if (globalElo >= 1050) {
            division = CC.GRAY + "Silver III";
        }
        if (globalElo >= 1075) {
            division = CC.GRAY + "Silver II";
        }
        if (globalElo >= 1100) {
            division = CC.GRAY + "Silver I";
        }
        if (globalElo >= 1150) {
            division = CC.YELLOW + "Gold V";
        }
        if (globalElo >= 1300) {
            division = CC.YELLOW + "Gold IV";
        }
        if (globalElo >= 1350) {
            division = CC.YELLOW + "Gold III";
        }
        if (globalElo >= 1400) {
            division = CC.YELLOW + "Gold II";
        }
        if (globalElo >= 1450) {
            division = CC.YELLOW + "Gold I";
        }
        if (globalElo >= 1500) {
            division = CC.AQUA + "Platinum V";
        }
        if (globalElo >= 1600) {
            division = CC.AQUA + "Platinum IV";
        }
        if (globalElo >= 1700) {
            division = CC.AQUA + "Platinum III";
        }
        if (globalElo >= 1800) {
            division = CC.AQUA + "Platinum II";
        }
        if (globalElo >= 1900) {
            division = CC.AQUA + "Platinum I";
        }
        if (globalElo >= 2000) {
            division = CC.GREEN + "Emerald V";
        }
        if (globalElo >= 2100) {
            division = CC.GREEN + "Emerald IV";
        }
        if (globalElo >= 2200) {
            division = CC.GREEN + "Emerald III";
        }
        if (globalElo >= 2300) {
            division = CC.GREEN + "Emerald II";
        }
        if (globalElo >= 2400) {
            division = CC.GREEN + "Emerald I";
        }
        if (globalElo >= 2500) {
            division = CC.BLUE + "Sapphire V";
        }
        if (globalElo >= 2600) {
            division = CC.BLUE + "Sapphire IV";
        }
        if (globalElo >= 2700) {
            division = CC.BLUE + "Sapphire III";
        }
        if (globalElo >= 2800) {
            division = CC.BLUE + "Sapphire II";
        }
        if (globalElo >= 2900) {
            division = CC.BLUE + "Sapphire I";
        }
        if (globalElo >= 3000) {
            division = CC.AQUA + "Champion";
        }
        return division;
    }

    private void renderPartyItems(Player player, PlayerInventory inventory, Party party) {
        QueueHandler queueHandler = PotPvPND.getInstance().getQueueHandler();

        if (party.isLeader(player.getUniqueId())) {
            int partySize = party.getMembers().size();

            if (partySize == 2) {
                if (!queueHandler.isQueuedUnranked(party)) {
                    inventory.setItem(1, QueueItems.JOIN_PARTY_UNRANKED_QUEUE_ITEM);
                    inventory.setItem(3, PartyItems.ASSIGN_CLASSES);
                } else {
                    inventory.setItem(1, QueueItems.LEAVE_PARTY_UNRANKED_QUEUE_ITEM);
                }

                if (!queueHandler.isQueuedRanked(party)) {
                    inventory.setItem(2, QueueItems.JOIN_PARTY_RANKED_QUEUE_ITEM);
                    inventory.setItem(3, PartyItems.ASSIGN_CLASSES);
                } else {
                    inventory.setItem(2, QueueItems.LEAVE_PARTY_RANKED_QUEUE_ITEM);
                }
            } else if (partySize > 2 && !queueHandler.isQueued(party)) {
                inventory.setItem(1, PartyItems.ASSIGN_CLASSES);
                inventory.setItem(2, PartyItems.START_TEAM_EVENTS);
            }

        } else {
            int partySize = party.getMembers().size();
            if (partySize >= 2) {
                inventory.setItem(1, PartyItems.ASSIGN_CLASSES);
            }
        }

        inventory.setItem(0, PartyItems.icon(party));
        inventory.setItem(4, PartyItems.OTHER_PARTIES_ITEM);
        inventory.setItem(7, KitItems.OPEN_EDITOR_ITEM);
        inventory.setItem(8, PartyItems.LEAVE_PARTY_ITEM);
    }

    private void renderSoloItems(Player player, PlayerInventory inventory) {
        RematchHandler rematchHandler = PotPvPND.getInstance().getRematchHandler();
        QueueHandler queueHandler = PotPvPND.getInstance().getQueueHandler();
        DuelHandler duelHandler = PotPvPND.getInstance().getDuelHandler();
        FollowHandler followHandler = PotPvPND.getInstance().getFollowHandler();
        LobbyHandler lobbyHandler = PotPvPND.getInstance().getLobbyHandler();

        boolean specMode = lobbyHandler.isInSpectatorMode(player);
        boolean followingSomeone = followHandler.getFollowing(player).isPresent();

        player.setAllowFlight(player.getGameMode() == GameMode.CREATIVE || specMode);

        if (specMode || followingSomeone) {
            inventory.setItem(5, LobbyItems.SPECTATE_MENU_ITEM);
            inventory.setItem(3, LobbyItems.SPECTATE_RANDOM_ITEM);
            inventory.setItem(4, LobbyItems.DISABLE_SPEC_MODE_ITEM);
            if (followingSomeone) {
                inventory.setItem(8, LobbyItems.UNFOLLOW_ITEM);
            }
        }  else {
            List<PlayerDuelInvite> playerInvites = duelHandler.getInvitesTo(player);
            if (!playerInvites.isEmpty()) {
                if (playerInvites.size() > 1) {
                    inventory.setItem(2, RematchItems.DUEL_SELECTOR);
                } else {
                    PlayerDuelInvite invite = playerInvites.get(0);
                    Player sender = Bukkit.getPlayer(invite.getSender());
                    if (!invite.isExpired() && sender != null) {
                        ItemStack itemStack = RematchItems.ACCEPT_DUEL_ITEM;
                        ItemUtils.setDisplayName(itemStack, GREEN + "Accept " + sender.getDisplayName());
                        inventory.setItem(2, itemStack);
                    }
                }
            }

            RematchData rematchData = rematchHandler.getRematchData(player);

            if (rematchData != null) {
                Player target = Bukkit.getPlayer(rematchData.getTarget());

                if (target != null) {
                    int index = 2;
                    ItemStack itemAt2 = inventory.getItem(2);
                    if (itemAt2 != null) {
                        index = 3;
                    }
                    if (duelHandler.findInvite(player, target) != null) {
                        // if we've sent an invite to them
                        inventory.setItem(index, RematchItems.SENT_REMATCH_ITEM);
                    } else if (duelHandler.findInvite(target, player) != null) {
                        // if they've sent us an invite
                        inventory.setItem(index, RematchItems.ACCEPT_REMATCH_ITEM);
                    } else {
                        // if no one has sent an invite
                        inventory.setItem(index, RematchItems.REQUEST_REMATCH_ITEM);
                    }
                }
            }

            if (queueHandler.isQueuedRanked(player.getUniqueId()) || queueHandler.isQueuedUnranked(player.getUniqueId())) {
                inventory.setItem(0, QueueItems.LEAVE_SOLO_QUEUE_ITEM);
            } else {
                inventory.setItem(0, QueueItems.JOIN_SOLO_UNRANKED_QUEUE_ITEM);
                inventory.setItem(1, QueueItems.JOIN_SOLO_RANKED_QUEUE_ITEM);
                if (player.hasPermission("potpvp.admin")) {
                    inventory.setItem(6, LobbyItems.MANAGE_ITEM);
                }
                inventory.setItem(7, LobbyItems.PLAYER_STATISTICS);
                inventory.setItem(8, KitItems.OPEN_EDITOR_ITEM);
                final ItemStack eventItem=EventItems.getEventItem();
                if (eventItem != null) {
                    inventory.setItem(4, eventItem);
                }
            }
        }
    }
}