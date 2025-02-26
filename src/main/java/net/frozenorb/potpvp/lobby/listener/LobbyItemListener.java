package net.frozenorb.potpvp.lobby.listener;

import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.command.ManageCommand;
import net.frozenorb.potpvp.follow.command.UnfollowCommand;
import net.frozenorb.potpvp.lobby.LobbyHandler;
import net.frozenorb.potpvp.lobby.LobbyItems;
import net.frozenorb.potpvp.lobby.menu.SpectateMenu;
import net.frozenorb.potpvp.lobby.menu.StatisticsMenu;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.potpvp.match.MatchState;
import net.frozenorb.potpvp.setting.menu.SettingsMenu;
import net.frozenorb.potpvp.util.ItemListener;
import net.frozenorb.potpvp.validation.PotPvPValidation;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public final class LobbyItemListener extends ItemListener {

    private final Map<UUID, Long> canUseRandomSpecItem = new HashMap<>();

    public LobbyItemListener(LobbyHandler lobbyHandler) {
        addHandler(LobbyItems.MANAGE_ITEM, p -> {
            if (p.hasPermission("potpvp.admin")) {
                ManageCommand.manage(p);
            }
        });

        addHandler(LobbyItems.DISABLE_SPEC_MODE_ITEM, player -> {
            if (lobbyHandler.isInLobby(player)) {
                lobbyHandler.setSpectatorMode(player, false);
            }
        });

        addHandler(LobbyItems.OPEN_SETTINGS_MENU, player -> {
            new SettingsMenu().openMenu(player);
        });

        addHandler(LobbyItems.SPECTATE_MENU_ITEM, player -> {
            if (PotPvPValidation.canUseSpectateItemIgnoreMatchSpectating(player)) {
                new SpectateMenu().openMenu(player);
            }
        });

        addHandler(LobbyItems.SPECTATE_RANDOM_ITEM, player -> {
            getRandomMatch(player, false);
        });

        addHandler(LobbyItems.SPECTATE_RANDOM_RANKED, player -> {
            getRandomMatch(player, true);
        });

        addHandler(LobbyItems.PLAYER_STATISTICS, player -> {
            new StatisticsMenu().openMenu(player);
        });

        addHandler(LobbyItems.UNFOLLOW_ITEM, UnfollowCommand::unfollow);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        canUseRandomSpecItem.remove(event.getPlayer().getUniqueId());
    }


    private void getRandomMatch(Player player, boolean ranked) {
        MatchHandler matchHandler = PotPvPND.getInstance().getMatchHandler();

        if (!PotPvPValidation.canUseSpectateItemIgnoreMatchSpectating(player)) {
            return;
        }

        if (canUseRandomSpecItem.getOrDefault(player.getUniqueId(), 0L) > System.currentTimeMillis()) {
            player.sendMessage(ChatColor.RED + "Please wait before doing this again!");
            return;
        }

        List<Match> matches = new ArrayList<>(matchHandler.getHostedMatches());
        matches.removeIf(m -> m.isSpectator(player.getUniqueId()) || m.getState() == MatchState.ENDING || ranked && !m.isRanked());

        if (matches.isEmpty()) {
            player.sendMessage(ChatColor.RED + "There are no matches available to spectate.");
        } else {
            Match currentlySpectating = matchHandler.getMatchSpectating(player);
            Match newSpectating = matches.get(ThreadLocalRandom.current().nextInt(matches.size()));

            if (currentlySpectating != null) {
                currentlySpectating.removeSpectator(player, false);
            }

            newSpectating.addSpectator(player, null);
            canUseRandomSpecItem.put(player.getUniqueId(), System.currentTimeMillis() + 3_000L);
        }
    }
}