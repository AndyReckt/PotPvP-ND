package net.frozenorb.potpvp.util;

import com.qrakn.morpheus.game.Game;
import com.qrakn.morpheus.game.GameQueue;
import lombok.experimental.UtilityClass;
import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.follow.FollowHandler;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.potpvp.party.Party;
import net.frozenorb.potpvp.party.PartyHandler;
import net.frozenorb.potpvp.setting.Setting;
import net.frozenorb.potpvp.setting.SettingHandler;
import net.frozenorb.potpvp.validation.PotPvPValidation;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.util.Optional;
import java.util.UUID;

@UtilityClass
public final class VisibilityUtils {

    public static void updateVisibilityFlicker(Player target) {
        for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
            target.hidePlayer(otherPlayer);
            otherPlayer.hidePlayer(target);
        }

        Bukkit.getScheduler().runTaskLater(PotPvPSI.getInstance(), () -> updateVisibility(target), 10L);
    }

    public static void updateVisibility(Player target) {
        for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
            if (shouldSeePlayer(otherPlayer, target)) {
                otherPlayer.showPlayer(target);
            } else {
                otherPlayer.hidePlayer(target);
            }

            if (shouldSeePlayer(target, otherPlayer)) {
                target.showPlayer(otherPlayer);
            } else {
                target.hidePlayer(otherPlayer);
            }
        }
    }

    public static boolean shouldHidePlayerFromTablist(Player viewer, Player target) {
        return target.hasMetadata("ModMode");
    }


    public static boolean shouldSeePlayer(Player viewer, Player target) {
        SettingHandler settingHandler = PotPvPSI.getInstance().getSettingHandler();
        FollowHandler followHandler = PotPvPSI.getInstance().getFollowHandler();
        PartyHandler partyHandler = PotPvPSI.getInstance().getPartyHandler();
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();

        Match targetMatch = matchHandler.getMatchPlayingOrSpectating(target);

        if (PotPvPValidation.isInGame(target)) {
            // we're in a game event
            Game game = GameQueue.INSTANCE.getCurrentGame(target);
            if (game.getPlayers().contains(target) && game.getPlayers().contains(viewer)
                && !game.getSpectators().contains(target)) {
                return true;
            }
            return game.getSpectators().contains(target) && game.getSpectators().contains(viewer);
        }

        if (targetMatch == null) {
            // we're not in a match so we hide other players based on their party/match
            Party targetParty = partyHandler.getParty(target);
            Optional<UUID> following = followHandler.getFollowing(viewer);

            boolean viewerPlayingMatch = matchHandler.isPlayingOrSpectatingMatch(viewer);
            boolean viewerSameParty = targetParty != null && targetParty.isMember(viewer.getUniqueId());
            boolean targetHasPermissionToBeSeen = target.hasPermission("potpvp.vip") && !target.isOp();
            boolean viewerFollowingTarget = following.isPresent() && following.get().equals(target.getUniqueId());

            Game game = GameQueue.INSTANCE.getCurrentGame(target);

            return (game != null && game.getPlayers().contains(target) && game.getPlayers().contains(viewer)) ||
                viewerPlayingMatch ||
                viewerSameParty ||
                viewerFollowingTarget ||
                targetHasPermissionToBeSeen;
        } else {
            // we're in a match so we only hide other spectators (if our settings say so)
            boolean targetIsSpectator = targetMatch.isSpectator(target.getUniqueId());
            boolean viewerSpecSetting = settingHandler.getSetting(viewer, Setting.VIEW_OTHER_SPECTATORS);
            boolean viewerIsSpectator = matchHandler.isSpectatingMatch(viewer);

            if (targetIsSpectator && viewerIsSpectator && target.hasMetadata("ModMode") && viewer.hasMetadata("ModMode")) {
                // if they're both in vanish and are spectating
                return true;
            }


            return !targetIsSpectator || (viewerSpecSetting && viewerIsSpectator && !target.hasMetadata("ModMode"));
        }
    }

}