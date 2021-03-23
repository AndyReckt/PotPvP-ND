package net.frozenorb.potpvp.tab;

import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.kt.tab.LayoutProvider;
import net.frozenorb.potpvp.kt.tab.TabLayout;
import net.frozenorb.potpvp.kt.util.PlayerUtils;
import net.frozenorb.potpvp.match.Match;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.function.BiConsumer;

public final class PotPvPLayoutProvider implements LayoutProvider {
    static final int MAX_TAB_Y=20;
    private static boolean testing;
    private final BiConsumer<Player, TabLayout> headerLayoutProvider;
    private final BiConsumer<Player, TabLayout> lobbyLayoutProvider;
    private final BiConsumer<Player, TabLayout> matchSpectatorLayoutProvider;
    private final BiConsumer<Player, TabLayout> matchParticipantLayoutProvider;

    public PotPvPLayoutProvider() {
        this.headerLayoutProvider=new HeaderLayoutProvider();
        this.lobbyLayoutProvider=new LobbyLayoutProvider();
        this.matchSpectatorLayoutProvider=new MatchSpectatorLayoutProvider();
        this.matchParticipantLayoutProvider=new MatchParticipantLayoutProvider();
    }

    @Override
    public TabLayout provide(@NotNull final Player player) {
        if (PotPvPND.getInstance() == null) {
            return TabLayout.create(player);
        }
        final TabLayout tabLayout=TabLayout.create(player);
        final Match match=PotPvPND.getInstance().getMatchHandler().getMatchPlayingOrSpectating(player);
        this.headerLayoutProvider.accept(player, tabLayout);
        if (match != null) {
            if (match.isSpectator(player.getUniqueId())) {
                this.matchSpectatorLayoutProvider.accept(player, tabLayout);
            } else {
                this.matchParticipantLayoutProvider.accept(player, tabLayout);
            }
        } else {
            this.lobbyLayoutProvider.accept(player, tabLayout);
        }
        return tabLayout;
    }

    static int getPingOrDefault(final UUID check) {
        final Player player=Bukkit.getPlayer(check);
        return (player != null) ? PlayerUtils.getPing(player) : 0;
    }

    static {
        PotPvPLayoutProvider.testing=true;
    }
}
