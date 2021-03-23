package net.frozenorb.potpvp.follow;

import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.follow.command.UnfollowCommand;
import net.frozenorb.potpvp.follow.listener.FollowGeneralListener;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.potpvp.match.MatchState;
import net.frozenorb.potpvp.util.InventoryUtils;
import net.frozenorb.potpvp.util.VisibilityUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class FollowHandler {

    // (follower -> target)
    private final Map<UUID, UUID> followingData = new ConcurrentHashMap<>();

    public FollowHandler() {
        Bukkit.getPluginManager().registerEvents(new FollowGeneralListener(this), PotPvPND.getInstance());
    }

    public Optional<UUID> getFollowing(Player player) {
        return Optional.ofNullable(followingData.get(player.getUniqueId()));
    }

    public void startFollowing(Player player, Player target) {
        this.getFollowing(player).ifPresent(fo -> UnfollowCommand.unfollow(player));

        followingData.put(player.getUniqueId(), target.getUniqueId());
        player.sendMessage(ChatColor.BLUE + "Now following " + ChatColor.YELLOW + target.getName() + ChatColor.BLUE + ", exit with /unfollow.");

        MatchHandler matchHandler = PotPvPND.getInstance().getMatchHandler();
        Match targetMatch = matchHandler.getMatchPlayingOrSpectating(target);

        if (targetMatch != null && targetMatch.getState() != MatchState.ENDING) {
            targetMatch.addSpectator(player, target);
        } else {
            InventoryUtils.resetInventoryDelayed(player);
            VisibilityUtils.updateVisibility(player);
            PotPvPND.getInstance().nametagEngine.reloadOthersFor(player);

            player.teleport(target);
        }
    }

    public void stopFollowing(Player player) {
        UUID prevTarget = followingData.remove(player.getUniqueId());

        if (prevTarget != null) {
            player.sendMessage(ChatColor.BLUE + "Stopped following " + ChatColor.YELLOW + PotPvPND.getInstance().getUuidCache().name(prevTarget) + ChatColor.BLUE + ".");
            InventoryUtils.resetInventoryDelayed(player);
            VisibilityUtils.updateVisibility(player);
            PotPvPND.getInstance().nametagEngine.reloadOthersFor(player);
        }
    }

    public Set<UUID> getFollowers(Player player) {
        Set<UUID> followers = new HashSet<>();

        followingData.forEach((follower, followed) -> {
            if (followed == player.getUniqueId()) {
                followers.add(follower);
            }
        });

        return followers;
    }

}