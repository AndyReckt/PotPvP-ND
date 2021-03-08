package net.frozenorb.potpvp.scoreboard;

import com.qrakn.morpheus.game.Game;
import com.qrakn.morpheus.game.GameQueue;
import com.qrakn.morpheus.game.GameState;
import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.kt.scoreboard.ScoreGetter;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.potpvp.setting.Setting;
import net.frozenorb.potpvp.setting.SettingHandler;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.function.BiConsumer;

final class MultiplexingScoreGetter implements ScoreGetter {
    private final BiConsumer<Player, LinkedList<String>> matchScoreGetter;
    private final BiConsumer<Player, LinkedList<String>> lobbyScoreGetter;
    private final BiConsumer<Player, LinkedList<String>> gameScoreGetter;

    MultiplexingScoreGetter(final BiConsumer<Player, LinkedList<String>> matchScoreGetter, final BiConsumer<Player, LinkedList<String>> lobbyScoreGetter, final BiConsumer<Player, LinkedList<String>> gameScoreGetter) {
        this.matchScoreGetter=matchScoreGetter;
        this.lobbyScoreGetter=lobbyScoreGetter;
        this.gameScoreGetter=gameScoreGetter;
    }

    @Override
    public void getScores(final LinkedList<String> scores, final Player player) {
        if (PotPvPSI.getInstance() == null) {
            return;
        }
        final MatchHandler matchHandler=PotPvPSI.getInstance().getMatchHandler();
        final SettingHandler settingHandler=PotPvPSI.getInstance().getSettingHandler();
        if (settingHandler.getSetting(player, Setting.SHOW_SCOREBOARD)) {
            if (matchHandler.isPlayingOrSpectatingMatch(player)) {
                this.matchScoreGetter.accept(player, scores);
            } else {
                final Game game=GameQueue.INSTANCE.getCurrentGame(player);
                if (game != null && game.getPlayers().contains(player) && game.getState() != GameState.ENDED) {
                    this.gameScoreGetter.accept(player, scores);
                } else {
                    this.lobbyScoreGetter.accept(player, scores);
                }
            }
        }
        if (!scores.isEmpty()) {
            scores.addFirst("&a&7&m--------------------");
            scores.add("");
            scores.add("&blunar.gg");
            scores.add("&f&7&m--------------------");
        }
    }
}
