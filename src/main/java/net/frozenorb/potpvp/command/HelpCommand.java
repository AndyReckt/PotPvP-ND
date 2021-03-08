package net.frozenorb.potpvp.command;

import com.google.common.collect.ImmutableList;
import net.frozenorb.potpvp.PotPvPLang;
import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.kt.command.Command;
import net.frozenorb.potpvp.match.MatchHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Generic /help command, changes message sent based on if sender is playing in
 * or spectating a match.
 */
public final class HelpCommand {

    private static final List<String> HELP_MESSAGE_HEADER = ImmutableList.of(
        ChatColor.DARK_PURPLE + PotPvPLang.LONG_LINE,
        "§c§lPractice Help",
        ChatColor.DARK_PURPLE + PotPvPLang.LONG_LINE,
        "§c§lRemember: §aMost things are clickable!",
        ""
    );

    private static final List<String> HELP_MESSAGE_LOBBY = ImmutableList.of(
        "§cCommon Commands:",
        "§a/duel <player> §7- Challenge a player to a duel",
        "§a/party invite <player> §7- Invite a player to a party",
        "",
        "§cOther Commands:",
        "§a/party help §7- Information on party commands",
        "§a/spectator §7- Toggle spectator mode",
        "§a/report <player> <reason> §7- Report a player for violating the rules",
        "§a/request <message> §7- Request assistance from a staff member"
    );

    private static final List<String> HELP_MESSAGE_MATCH = ImmutableList.of(
        "§cCommon Commands:",
        "§a/spectate <player> §7- Spectate a player in a match",
        "§a/report <player> <reason> §7- Report a player for violating the rules",
        "§a/request <message> §7- Request assistance from a staff member"
    );

    private static final List<String> HELP_MESSAGE_FOOTER = ImmutableList.of(
        "",
        "§cServer Information:",
        PotPvPSI.getInstance().getDominantColor() == ChatColor.LIGHT_PURPLE ? "§cOfficial Teamspeak §7- §akoru.rip" : "§cOfficial Teamspeak §7- §ats.koru.rip",
        PotPvPSI.getInstance().getDominantColor() == ChatColor.LIGHT_PURPLE ? "§cOfficial Rules §7- §akoru.rip" : "§cOfficial Rules §7- §akoru.rip",
        PotPvPSI.getInstance().getDominantColor() == ChatColor.LIGHT_PURPLE ? "§cStore §7- §astore.koru.rip" : "§cStore §7- §astore.koru.rip",
        "§cPractice Leaderboards §7- §ahttps://www.koru.rip/stats/practice",
        ChatColor.DARK_PURPLE + PotPvPLang.LONG_LINE
    );

    @Command(names = {"help", "?", "halp", "helpme"}, permission = "")
    public static void help(Player sender) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();

        HELP_MESSAGE_HEADER.forEach(sender::sendMessage);

        if (matchHandler.isPlayingOrSpectatingMatch(sender)) {
            HELP_MESSAGE_MATCH.forEach(sender::sendMessage);
        } else {
            HELP_MESSAGE_LOBBY.forEach(sender::sendMessage);
        }

        HELP_MESSAGE_FOOTER.forEach(sender::sendMessage);
    }

}
