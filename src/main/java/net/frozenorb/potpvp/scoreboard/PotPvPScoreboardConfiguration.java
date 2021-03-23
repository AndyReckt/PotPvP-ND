package net.frozenorb.potpvp.scoreboard;

import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.kt.scoreboard.ScoreboardConfiguration;
import net.frozenorb.potpvp.kt.scoreboard.TitleGetter;
import org.bukkit.ChatColor;

public final class PotPvPScoreboardConfiguration {
    public static ScoreboardConfiguration create() {
        return new ScoreboardConfiguration(TitleGetter.forStaticString(PotPvPND.getInstance().getDominantColor().toString() + ChatColor.BOLD + PotPvPND.getInstance().getMainConfig().getString("Practice.Scoreboard-Header")), new MultiplexingScoreGetter(new MatchScoreGetter(), new LobbyScoreGetter(), new GameScoreGetter()));
    }
}
