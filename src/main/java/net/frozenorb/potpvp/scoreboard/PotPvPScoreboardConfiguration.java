package net.frozenorb.potpvp.scoreboard;

import net.frozenorb.potpvp.kt.scoreboard.ScoreboardConfiguration;
import net.frozenorb.potpvp.kt.scoreboard.TitleGetter;

public final class PotPvPScoreboardConfiguration {
    public static ScoreboardConfiguration create() {
        return new ScoreboardConfiguration(TitleGetter.forStaticString("&b&lNA Practice"), new MultiplexingScoreGetter(new MatchScoreGetter(), new LobbyScoreGetter(), new GameScoreGetter()));
    }
}
