package net.frozenorb.potpvp.command;

import com.qrakn.morpheus.game.Game;
import com.qrakn.morpheus.game.GameQueue;
import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.follow.FollowHandler;
import net.frozenorb.potpvp.kt.command.Command;
import net.frozenorb.potpvp.kt.command.data.parameter.Param;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.potpvp.party.Party;
import net.frozenorb.potpvp.party.PartyHandler;
import net.frozenorb.potpvp.setting.Setting;
import net.frozenorb.potpvp.setting.SettingHandler;
import net.frozenorb.potpvp.util.VisibilityUtils;
import net.frozenorb.potpvp.validation.PotPvPValidation;
import net.minecraft.server.v1_7_R4.EntityTracker;
import net.minecraft.server.v1_7_R4.EntityTrackerEntry;
import net.minecraft.server.v1_7_R4.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_7_R4.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

public final class VDebugCommand {

    @Command(names = {"vdebug"}, permission = "op")
    public static void vdebug(Player sender, @Param(name = "a") Player a, @Param(name = "b") Player b, @Param(name = "modify", defaultValue = "0") int modify) {
        CraftPlayer aCraft = (CraftPlayer) a;
        CraftPlayer bCraft = (CraftPlayer) b;
        EntityTracker tracker = ((WorldServer) aCraft.getHandle().world).tracker;
        EntityTrackerEntry aTracker = (EntityTrackerEntry) tracker.trackedEntities.get(aCraft.getHandle().getId());
        EntityTrackerEntry bTracker = (EntityTrackerEntry) tracker.trackedEntities.get(bCraft.getHandle().getId());

        if (modify == 1) {
            a.showPlayer(b);
            b.showPlayer(a);

            sender.sendMessage(ChatColor.RED + "Performed soft modify.");
            return;
        } else if (modify == 2) {
            aCraft.getHandle().playerConnection.sendPacket(PacketPlayOutPlayerInfo.addPlayer(bCraft.getHandle()));
            bCraft.getHandle().playerConnection.sendPacket(PacketPlayOutPlayerInfo.addPlayer(aCraft.getHandle()));

            sender.sendMessage(ChatColor.RED + "Performed hard modify.");
            return;
        } else if (modify == 3) {
            a.hidePlayer(b);
            b.hidePlayer(a);

            Bukkit.getScheduler().runTaskLater(PotPvPND.getInstance(), () -> {
                a.showPlayer(b);
                b.showPlayer(a);
            }, 10L);

            sender.sendMessage(ChatColor.RED + "Performed flicker modify.");
            return;
        }

        sender.sendMessage(ChatColor.AQUA.toString() + ChatColor.UNDERLINE + a.getName() + " <-> " + b.getName() + ":");
        sender.sendMessage("");
        sender.sendMessage(ChatColor.BLUE + "a Validation: " + ChatColor.WHITE + (aCraft.getHandle().playerConnection != null && !a.equals(b)));
        sender.sendMessage(ChatColor.BLUE + "b Validation: " + ChatColor.WHITE + (bCraft.getHandle().playerConnection != null && !b.equals(a)));

        sender.sendMessage(ChatColor.BLUE + "a.canSee(b): " + ChatColor.WHITE + a.canSee(b));
        sender.sendMessage(ChatColor.BLUE + "b.canSee(a): " + ChatColor.WHITE + b.canSee(a));

        sender.sendMessage(ChatColor.BLUE + "a Tracker Entry: " + ChatColor.WHITE + aTracker);
        sender.sendMessage(ChatColor.BLUE + "b Tracker Entry: " + ChatColor.WHITE + bTracker);

        sender.sendMessage(ChatColor.BLUE + "aTracker.trackedPlayers.contains(b): " + ChatColor.WHITE + aTracker.trackedPlayers.contains(bCraft.getHandle()));
        sender.sendMessage(ChatColor.BLUE + "bTracker.trackedPlayers.contains(a): " + ChatColor.WHITE + bTracker.trackedPlayers.contains(aCraft.getHandle()));
    }

    @Command(names = {"wvcheck"}, permission = "kore.staff")
    public static void vcheck(Player sender, @Param(name = "player1") Player player1, @Param(name = "player2") Player player2) {

        if (VisibilityUtils.shouldSeePlayer(player1, player2)) {
            sender.sendMessage(player1.getName() + " should see " + player2.getName());
        }

        if (VisibilityUtils.shouldSeePlayer(player2, player1)) {
            sender.sendMessage(player2.getName() + " should see " + player1.getName());
        }

        sender.sendMessage("Checking for p1->p2");
        sender.sendMessage(debug(player1, player2));

        sender.sendMessage("");
        sender.sendMessage("Checking for p2->p1");
        sender.sendMessage(debug(player2, player1));
    }

    @Command(names = {"vupdate"}, permission = "kore.staff")
    public static void vupdate(Player sender, @Param(name = "player1") Player player1, @Param(name = "player2") Player player2) {

        VisibilityUtils.updateVisibilityFlicker(player1);
        VisibilityUtils.updateVisibilityFlicker(player2);

        sender.sendMessage("Updated visibility for both players");
    }

    private static String debug(Player viewer, Player target) {
        SettingHandler settingHandler = PotPvPND.getInstance().getSettingHandler();
        FollowHandler followHandler = PotPvPND.getInstance().getFollowHandler();
        PartyHandler partyHandler = PotPvPND.getInstance().getPartyHandler();
        MatchHandler matchHandler = PotPvPND.getInstance().getMatchHandler();

        Match targetMatch = matchHandler.getMatchPlayingOrSpectating(target);

        if (PotPvPValidation.isInGame(target)) {
            // we're in a game event
            Game game = GameQueue.INSTANCE.getCurrentGame(target);
            if (game.getPlayers().contains(target) && game.getPlayers().contains(viewer)
                && !game.getSpectators().contains(target)) {
                return "They should see eachother, same game";
            }
            if (game.getSpectators().contains(target) && game.getSpectators().contains(viewer)) {
                return "Both spectators in a game";
            } else {
                return "Should not see eachother in the game";
            }
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

            String strings = "";

            if (targetHasPermissionToBeSeen) {
                strings += "Target has permission to be seen in lobby. ";
            }

            if (viewerFollowingTarget) {
                strings += "Viewer following target. ";
            }

            if (viewerSameParty) {
                strings += "Viewer same party. ";
            }

            if (viewerPlayingMatch) {
                strings += "Viewer playing match. ";
            }

            return strings;

        } else {
            // we're in a match so we only hide other spectators (if our settings say so)
            boolean targetIsSpectator = targetMatch.isSpectator(target.getUniqueId());
            boolean viewerSpecSetting = settingHandler.getSetting(viewer, Setting.VIEW_OTHER_SPECTATORS);
            boolean viewerIsSpectator = matchHandler.isSpectatingMatch(viewer);

            if (targetIsSpectator && viewerIsSpectator) {
                // if they're both in vanish and are spectating
                return "They are both in vanish and spectating. ";
            }

            if (!targetIsSpectator) {
                return "Target is not spectator. SHOULD be visible. ";
            } else {
                if (viewerSpecSetting && viewerIsSpectator) {
                    return "They should see eachother (spectator)";
                }
            }
        }
        return "Not visible somehow";
    }

}