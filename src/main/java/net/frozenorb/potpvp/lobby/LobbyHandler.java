package net.frozenorb.potpvp.lobby;

import com.qrakn.morpheus.game.Game;
import com.qrakn.morpheus.game.GameQueue;
import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.follow.FollowHandler;
import net.frozenorb.potpvp.follow.command.UnfollowCommand;
import net.frozenorb.potpvp.kit.listener.KitEditorListener;
import net.frozenorb.potpvp.lobby.listener.LobbyGeneralListener;
import net.frozenorb.potpvp.lobby.listener.LobbyItemListener;
import net.frozenorb.potpvp.lobby.listener.LobbyParkourListener;
import net.frozenorb.potpvp.lobby.listener.LobbySpecModeListener;
import net.frozenorb.potpvp.util.InventoryUtils;
import net.frozenorb.potpvp.util.PatchedPlayerUtils;
import net.frozenorb.potpvp.util.VisibilityUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

public final class LobbyHandler {

    /**
     * Stores players who are in "spectator mode", which gives them fly mode
     * and a different lobby hotbar. This setting is purely cosmetic, it doesn't
     * change what a player can/can't do (with the exception of not giving them
     * certain clickable items - but that's just a UX decision)
     */
    private final Set<UUID> spectatorMode = new HashSet<>();
    private final Map<UUID, Long> returnedToLobby = new HashMap<>();

    public LobbyHandler() {
        Bukkit.getPluginManager().registerEvents(new LobbyGeneralListener(this), PotPvPND.getInstance());
        Bukkit.getPluginManager().registerEvents(new LobbyItemListener(this), PotPvPND.getInstance());
        Bukkit.getPluginManager().registerEvents(new LobbySpecModeListener(), PotPvPND.getInstance());
        Bukkit.getPluginManager().registerEvents(new LobbyParkourListener(), PotPvPND.getInstance());
    }

    /**
     * Returns a player to the main lobby. This includes performing
     * the teleport, clearing their inventory, updating their nametag,
     * etc. etc.
     *
     * @param player the player who is to be returned
     */
    public void returnToLobby(Player player) {
        if (Bukkit.getPlayer(player.getUniqueId()) != null && player.isOnline()) {
            player.setWalkSpeed(0.2F);
            returnToLobbySkipItemSlot(player);
            player.getInventory().setHeldItemSlot(0);
        }
    }

    private void returnToLobbySkipItemSlot(Player player) {
        player.teleport(getLobbyLocation());

        PotPvPND.getInstance().nametagEngine.reloadPlayer(player);
        PotPvPND.getInstance().nametagEngine.reloadOthersFor(player);

        VisibilityUtils.updateVisibility(player);
        PatchedPlayerUtils.resetInventory(player, GameMode.SURVIVAL, true);
        InventoryUtils.resetInventoryDelayed(player);
        player.setGameMode(GameMode.SURVIVAL);
        returnedToLobby.put(player.getUniqueId(), System.currentTimeMillis());
    }


    public long getLastLobbyTime(Player player) {
        return returnedToLobby.getOrDefault(player.getUniqueId(), 0L);
    }

    public boolean isInLobby(Player player) {

        Game game = GameQueue.INSTANCE.getCurrentGame(player);
        boolean isInGame = game != null && game.getPlayers().contains(player);

        boolean isInMatch = PotPvPND.getInstance().getMatchHandler().isPlayingOrSpectatingMatch(player);

        boolean isEditingKit = KitEditorListener.isEditingKit(player);

        return !(isInMatch || isInGame || isEditingKit);
    }

    public boolean isInSpectatorMode(Player player) {
        return spectatorMode.contains(player.getUniqueId());
    }

    public void setSpectatorMode(Player player, boolean mode) {
        boolean changed;

        if (mode) {
            changed = spectatorMode.add(player.getUniqueId());
        } else {
            FollowHandler followHandler = PotPvPND.getInstance().getFollowHandler();
            followHandler.getFollowing(player).ifPresent(i -> UnfollowCommand.unfollow(player));

            changed = spectatorMode.remove(player.getUniqueId());
        }

        if (changed) {
            InventoryUtils.resetInventoryNow(player);

            if (!mode) {
                returnToLobbySkipItemSlot(player);
            }
        }
    }

    public Location getLobbyLocation() {
        Location spawn = Bukkit.getWorlds().get(0).getSpawnLocation();
        spawn.add(0.5, 0, 0.5); // 'prettify' so players spawn in middle of block
        return spawn;
    }

}