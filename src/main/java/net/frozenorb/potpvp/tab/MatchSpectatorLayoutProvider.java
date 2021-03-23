package net.frozenorb.potpvp.tab;

import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.kt.tab.TabLayout;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.MatchTeam;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.BiConsumer;

final class MatchSpectatorLayoutProvider implements BiConsumer<Player, TabLayout> {
    @Override
    public void accept(final Player player, final TabLayout tabLayout) {
        final Match match=PotPvPND.getInstance().getMatchHandler().getMatchSpectating(player);
        final MatchTeam oldTeam=match.getTeam(player.getUniqueId());
        final List<MatchTeam> teams=match.getTeams();
        if (teams.size() == 2) {
            final MatchTeam teamOne=teams.get(0);
            final MatchTeam teamTwo=teams.get(1);
            final boolean duel=teamOne.getAllMembers().size() == 1 && teamTwo.getAllMembers().size() == 1;
            if (oldTeam != null) {
                final MatchTeam ourTeam=(teamOne == oldTeam) ? teamOne : teamTwo;
                final MatchTeam otherTeam=(teamOne == ourTeam) ? teamTwo : teamOne;
                if (!duel) {
                    tabLayout.set(0, 3, ChatColor.GREEN + ChatColor.BOLD.toString() + "Team " + ChatColor.GREEN + "(" + ourTeam.getAliveMembers().size() + "/" + ourTeam.getAllMembers().size() + ")");
                } else {
                    tabLayout.set(0, 3, ChatColor.GREEN + ChatColor.BOLD.toString() + "You");
                }
                this.renderTeamMemberOverviewEntries(tabLayout, ourTeam, 0, 4, ChatColor.GREEN);
                if (!duel) {
                    tabLayout.set(2, 3, ChatColor.RED + ChatColor.BOLD.toString() + "Enemies " + ChatColor.RED + "(" + otherTeam.getAliveMembers().size() + "/" + otherTeam.getAllMembers().size() + ")");
                } else {
                    tabLayout.set(2, 3, ChatColor.RED + ChatColor.BOLD.toString() + "Opponent");
                }
                this.renderTeamMemberOverviewEntries(tabLayout, otherTeam, 2, 4, ChatColor.RED);
            } else {
                if (!duel) {
                    tabLayout.set(0, 3, ChatColor.AQUA + ChatColor.BOLD.toString() + "Team One (" + teamOne.getAliveMembers().size() + "/" + teamOne.getAllMembers().size() + ")");
                } else {
                    tabLayout.set(0, 3, ChatColor.AQUA + ChatColor.BOLD.toString() + "Player One");
                }
                this.renderTeamMemberOverviewEntries(tabLayout, teamOne, 0, 4, ChatColor.AQUA);
                if (!duel) {
                    tabLayout.set(2, 3, ChatColor.RED + ChatColor.BOLD.toString() + "Team Two (" + teamTwo.getAliveMembers().size() + "/" + teamTwo.getAllMembers().size() + ")");
                } else {
                    tabLayout.set(2, 3, ChatColor.RED + ChatColor.BOLD.toString() + "Player Two");
                }
                this.renderTeamMemberOverviewEntries(tabLayout, teamTwo, 2, 4, ChatColor.RED);
            }
        } else {
            tabLayout.set(1, 3, ChatColor.AQUA + ChatColor.BOLD.toString() + "Party FFA");
            int x=0;
            int y=4;
            Map<String, Integer> entries=new LinkedHashMap<>();
            if (oldTeam != null) {
                entries=this.renderTeamMemberOverviewLines(oldTeam, ChatColor.GREEN);
                final Map<String, Integer> deadLines=new LinkedHashMap<>();
                for ( final MatchTeam otherTeam2 : match.getTeams() ) {
                    if (otherTeam2 == oldTeam) {
                        continue;
                    }
                    for ( final UUID enemy : otherTeam2.getAllMembers() ) {
                        if (otherTeam2.isAlive(enemy)) {
                            entries.put(ChatColor.RED + PotPvPND.getInstance().getUuidCache().name(enemy), PotPvPLayoutProvider.getPingOrDefault(enemy));
                        } else {
                            deadLines.put("&7&m" + PotPvPND.getInstance().getUuidCache().name(enemy), PotPvPLayoutProvider.getPingOrDefault(enemy));
                        }
                    }
                }
                entries.putAll(deadLines);
            } else {
                final Map<String, Integer> deadLines=new LinkedHashMap<>();
                for ( final MatchTeam team : match.getTeams() ) {
                    for ( final UUID enemy : team.getAllMembers() ) {
                        if (team.isAlive(enemy)) {
                            entries.put("&c" + PotPvPND.getInstance().getUuidCache().name(enemy), PotPvPLayoutProvider.getPingOrDefault(enemy));
                        } else {
                            deadLines.put("&7&m" + PotPvPND.getInstance().getUuidCache().name(enemy), PotPvPLayoutProvider.getPingOrDefault(enemy));
                        }
                    }
                }
                entries.putAll(deadLines);
            }
            final List<Map.Entry<String, Integer>> result=new ArrayList<>(entries.entrySet());
            int index=0;
            while (index < result.size()) {
                final Map.Entry<String, Integer> entry=result.get(index);
                tabLayout.set(x++, y, entry.getKey(), entry.getValue());
                if (x == 3 && y == 20) {
                    int aliveLeft=0;
                    for ( int i=index; i < result.size(); ++i ) {
                        final String currentEntry=result.get(i).getKey();
                        final boolean dead=ChatColor.getLastColors(currentEntry).equals(ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString());
                        if (!dead) {
                            ++aliveLeft;
                        }
                    }
                    if (aliveLeft != 0 && aliveLeft != 1) {
                        tabLayout.set(x, y, ChatColor.GREEN + "+" + aliveLeft);
                        break;
                    }
                    break;
                } else {
                    if (x == 3) {
                        x=0;
                        ++y;
                    }
                    ++index;
                }
            }
        }
    }

    private void renderTeamMemberOverviewEntries(final TabLayout layout, final MatchTeam team, final int column, final int start, final ChatColor color) {
        final List<Map.Entry<String, Integer>> result=new ArrayList<>(this.renderTeamMemberOverviewLines(team, color).entrySet());
        int spotsLeft=20 - start;
        int y=start;
        int index=0;
        while (index < result.size()) {
            final Map.Entry<String, Integer> entry=result.get(index);
            if (spotsLeft == 1) {
                int aliveLeft=0;
                for ( int i=index; i < result.size(); ++i ) {
                    final String currentEntry=result.get(i).getKey();
                    final boolean dead=!ChatColor.getLastColors(currentEntry).equals(color.toString());
                    if (!dead) {
                        ++aliveLeft;
                    }
                }
                if (aliveLeft == 0) {
                    break;
                }
                if (aliveLeft == 1) {
                    layout.set(column, y, entry.getKey(), entry.getValue());
                    break;
                }
                layout.set(column, y, color + "+" + aliveLeft);
                break;
            } else {
                layout.set(column, y, entry.getKey(), entry.getValue());
                ++y;
                --spotsLeft;
                ++index;
            }
        }
    }

    private Map<String, Integer> renderTeamMemberOverviewLines(final MatchTeam team, final ChatColor aliveColor) {
        final Map<String, Integer> aliveLines=new LinkedHashMap<>();
        final Map<String, Integer> deadLines=new LinkedHashMap<>();
        for ( final UUID member : team.getAllMembers() ) {
            final int ping=PotPvPLayoutProvider.getPingOrDefault(member);
            if (team.isAlive(member)) {
                aliveLines.put(aliveColor + PotPvPND.getInstance().getUuidCache().name(member), ping);
            } else {
                deadLines.put("&7&m" + PotPvPND.getInstance().getUuidCache().name(member), ping);
            }
        }
        final Map<String, Integer> result=new LinkedHashMap<>();
        result.putAll(aliveLines);
        result.putAll(deadLines);
        return result;
    }
}
