package net.frozenorb.potpvp.scoreboard;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.kittype.HealingMethod;
import net.frozenorb.potpvp.kt.scoreboard.ScoreFunction;
import net.frozenorb.potpvp.kt.util.PlayerUtils;
import net.frozenorb.potpvp.kt.util.TimeUtils;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.potpvp.match.MatchState;
import net.frozenorb.potpvp.match.MatchTeam;
import net.frozenorb.potpvp.pvpclasses.pvpclasses.ArcherClass;
import net.frozenorb.potpvp.pvpclasses.pvpclasses.BardClass;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.BiConsumer;

final class MatchScoreGetter implements BiConsumer<Player, LinkedList<String>> {
    private Map<UUID, Integer> healsLeft;

    MatchScoreGetter() {
        Bukkit.getScheduler().runTaskTimer(PotPvPSI.getInstance(), () -> {
            MatchHandler matchHandler=PotPvPSI.getInstance().getMatchHandler();
            Map<UUID, Integer> newHealsLeft=new HashMap<>();

            for ( Player player : Bukkit.getOnlinePlayers() ) {
                Match playing=matchHandler.getMatchPlaying(player);

                if (playing == null) {
                    continue;
                }

                HealingMethod healingMethod=playing.getKitType().getHealingMethod();

                if (healingMethod == null) {
                    continue;
                }

                int count=healingMethod.count(player.getInventory().getContents());
                newHealsLeft.put(player.getUniqueId(), count);
            }

            this.healsLeft=newHealsLeft;
        }, 10L, 10L);
    }

    @Override
    public void accept(final Player player, final LinkedList<String> scores) {
        final MatchHandler matchHandler=PotPvPSI.getInstance().getMatchHandler();
        final Match match=matchHandler.getMatchPlayingOrSpectating(player);
        if (match == null) {
            return;
        }
        final boolean participant=match.getTeam(player.getUniqueId()) != null;
        boolean renderPing=false;
        if (participant) {
            renderPing=this.renderParticipantLines(scores, match, player);
        } else {
            final MatchTeam previousTeam=match.getPreviousTeam(player.getUniqueId());
            this.renderSpectatorLines(scores, match, previousTeam);
        }
        this.renderMetaLines(scores, match, participant);
        this.renderPingLines(scores, match, player);
    }

    private boolean renderParticipantLines(final List<String> scores, final Match match, final Player player) {
        final List<MatchTeam> teams=match.getTeams();
        if (teams.size() != 2) {
            return false;
        }
        final MatchTeam ourTeam=match.getTeam(player.getUniqueId());
        final MatchTeam otherTeam=(teams.get(0) == ourTeam) ? teams.get(1) : teams.get(0);
        final int ourTeamSize=ourTeam.getAllMembers().size();
        final int otherTeamSize=otherTeam.getAllMembers().size();
        if (ourTeamSize == 1 && otherTeamSize == 1) {
            this.render1v1MatchLines(scores, otherTeam);
            this.renderPingLines(scores, match, player);
        } else if (ourTeamSize <= 2 && otherTeamSize <= 2) {
            this.render2v2MatchLines(scores, ourTeam, otherTeam, player, match.getKitType().getHealingMethod());
        } else if (ourTeamSize <= 4 && otherTeamSize <= 4) {
            this.render4v4MatchLines(scores, ourTeam, otherTeam);
        } else if (ourTeam.getAllMembers().size() <= 9) {
            this.renderLargeMatchLines(scores, ourTeam, otherTeam);
        } else {
            this.renderJumboMatchLines(scores, ourTeam, otherTeam);
        }
        final String archerMarkScore=this.getArcherMarkScore(player);
        final String bardEffectScore=this.getBardEffectScore(player);
        final String bardEnergyScore=this.getBardEnergyScore(player);
        if (archerMarkScore != null) {
            scores.add("&fArcher Mark&7: &c" + archerMarkScore);
        }
        if (bardEffectScore != null) {
            scores.add("&fBard Effect&7: &c" + bardEffectScore);
        }
        if (bardEnergyScore != null) {
            scores.add("&fBard Energy&7: &c" + bardEnergyScore);
        }
        return false;
    }

    private void render1v1MatchLines(final List<String> scores, final MatchTeam otherTeam) {
        scores.add("&fOpponent: &b" + PotPvPSI.getInstance().getUuidCache().name(otherTeam.getFirstMember()));
    }

    private void render2v2MatchLines(final List<String> scores, final MatchTeam ourTeam, final MatchTeam otherTeam, final Player player, final HealingMethod healingMethod) {
        UUID partnerUuid=null;
        for ( final UUID teamMember : ourTeam.getAllMembers() ) {
            if (teamMember != player.getUniqueId()) {
                partnerUuid=teamMember;
                break;
            }
        }
        if (partnerUuid != null) {
            String namePrefix;
            String healthStr;
            String healsStr;
            if (ourTeam.isAlive(partnerUuid)) {
                final Player partnerPlayer=Bukkit.getPlayer(partnerUuid);
                final double health=Math.round(partnerPlayer.getHealth()) / 2.0;
                final int heals=this.healsLeft.getOrDefault(partnerUuid, 0);
                ChatColor healthColor;
                if (health > 8.0) {
                    healthColor=ChatColor.GREEN;
                } else if (health > 6.0) {
                    healthColor=ChatColor.YELLOW;
                } else if (health > 4.0) {
                    healthColor=ChatColor.AQUA;
                } else if (health > 1.0) {
                    healthColor=ChatColor.RED;
                } else {
                    healthColor=ChatColor.DARK_RED;
                }
                ChatColor healsColor;
                if (heals > 20) {
                    healsColor=ChatColor.GREEN;
                } else if (heals > 12) {
                    healsColor=ChatColor.YELLOW;
                } else if (heals > 8) {
                    healsColor=ChatColor.AQUA;
                } else if (heals > 3) {
                    healsColor=ChatColor.RED;
                } else {
                    healsColor=ChatColor.DARK_RED;
                }
                namePrefix="&a";
                healthStr=healthColor.toString() + health + " *\u2764*" + ChatColor.GRAY;
                if (healingMethod != null) {
                    healsStr=" &l\u23d0 " + healsColor.toString() + heals + " " + ((heals == 1) ? healingMethod.getShortSingular() : healingMethod.getShortPlural());
                } else {
                    healsStr="";
                }
            } else {
                namePrefix="&7&m";
                healthStr="&4RIP";
                healsStr="";
            }
            scores.add(namePrefix + PotPvPSI.getInstance().getUuidCache().name(partnerUuid));
            scores.add(healthStr + healsStr);
            scores.add("&c");
        }
        scores.add("&c&lOpponents");
        scores.addAll(this.renderTeamMemberOverviewLines(otherTeam));
        if (PotPvPSI.getInstance().getMatchHandler().getMatchPlaying(player).getState() == MatchState.IN_PROGRESS) {
            scores.add("&b");
        }
    }

    private void render4v4MatchLines(final List<String> scores, final MatchTeam ourTeam, final MatchTeam otherTeam) {
        scores.add("&aTeam &a(" + ourTeam.getAliveMembers().size() + "/" + ourTeam.getAllMembers().size() + ")");
        scores.addAll(this.renderTeamMemberOverviewLinesWithHearts(ourTeam));
        scores.add("&b");
        scores.add("&cOpponents &c(" + otherTeam.getAliveMembers().size() + "/" + otherTeam.getAllMembers().size() + ")");
        scores.addAll(this.renderTeamMemberOverviewLines(otherTeam));
        if (PotPvPSI.getInstance().getMatchHandler().getMatchPlaying(Bukkit.getPlayer(ourTeam.getFirstAliveMember())).getState() == MatchState.IN_PROGRESS) {
            scores.add("&c");
        }
    }

    private void renderLargeMatchLines(final List<String> scores, final MatchTeam ourTeam, final MatchTeam otherTeam) {
        scores.add("&aTeam &a(" + ourTeam.getAliveMembers().size() + "/" + ourTeam.getAllMembers().size() + ")");
        scores.addAll(this.renderTeamMemberOverviewLinesWithHearts(ourTeam));
        scores.add("&b");
        scores.add("&cOpponents: &c(" + otherTeam.getAliveMembers().size() + "/" + otherTeam.getAllMembers().size() + ")");
    }

    private void renderJumboMatchLines(final List<String> scores, final MatchTeam ourTeam, final MatchTeam otherTeam) {
        scores.add("&aTeam: &f" + ourTeam.getAliveMembers().size() + "/" + ourTeam.getAllMembers().size());
        scores.add("&cOpponents: &f" + otherTeam.getAliveMembers().size() + "/" + otherTeam.getAllMembers().size());
    }

    private void renderSpectatorLines(final List<String> scores, final Match match, final MatchTeam oldTeam) {
        final String rankedStr=match.isRanked() ? " (R)" : "";
        scores.add("&eKit: &a" + match.getKitType().getDisplayName() + rankedStr);
        final List<MatchTeam> teams=match.getTeams();
        if (teams.size() == 2) {
            final MatchTeam teamOne=teams.get(0);
            final MatchTeam teamTwo=teams.get(1);
            if (teamOne.getAllMembers().size() != 1 && teamTwo.getAllMembers().size() != 1) {
                if (oldTeam == null) {
                    scores.add("&fTeam One: &b" + teamOne.getAliveMembers().size() + "/" + teamOne.getAllMembers().size());
                    scores.add("&fTeam Two: &b" + teamTwo.getAliveMembers().size() + "/" + teamTwo.getAllMembers().size());
                } else {
                    final MatchTeam otherTeam=(oldTeam == teamOne) ? teamTwo : teamOne;
                    scores.add("&fTeam: &a" + oldTeam.getAliveMembers().size() + "/" + oldTeam.getAllMembers().size());
                    scores.add("&fOpponents: &c" + otherTeam.getAliveMembers().size() + "/" + otherTeam.getAllMembers().size());
                }
            }
        }
    }

    private void renderMetaLines(final List<String> scores, final Match match, final boolean participant) {
        final Date startedAt=match.getStartedAt();
        final Date endedAt=match.getEndedAt();
        if (startedAt == null) {
            return;
        }
        final String formattedDuration=TimeUtils.formatLongIntoMMSS(ChronoUnit.SECONDS.between(startedAt.toInstant(), (endedAt == null) ? Instant.now() : endedAt.toInstant()));
        scores.add(PotPvPSI.getInstance().getDominantColor() + "&fDuration: &b" + formattedDuration);
    }

    private void renderPingLines(final List<String> scores, final Match match, final Player ourPlayer) {
        if (Boolean.TRUE) {
            return;
        }
        final List<MatchTeam> teams=match.getTeams();
        if (teams.size() == 2) {
            final MatchTeam firstTeam=teams.get(0);
            final MatchTeam secondTeam=teams.get(1);
            final Set<UUID> firstTeamPlayers=firstTeam.getAllMembers();
            final Set<UUID> secondTeamPlayers=secondTeam.getAllMembers();
            if (firstTeamPlayers.size() == 1 && secondTeamPlayers.size() == 1) {
                scores.add("");
                scores.add("&fYour Ping: &a" + PlayerUtils.getPing(ourPlayer));
                final Player otherPlayer=Bukkit.getPlayer((match.getTeam(ourPlayer.getUniqueId()) == firstTeam) ? secondTeam.getFirstMember() : firstTeam.getFirstMember());
                if (otherPlayer == null) {
                    return;
                }
                scores.add("&fTheir Ping: &c" + PlayerUtils.getPing(otherPlayer));
            }
        }
    }

    private List<String> renderTeamMemberOverviewLinesWithHearts(final MatchTeam team) {
        final List<String> aliveLines=new ArrayList<String>();
        final List<String> deadLines=new ArrayList<String>();
        for ( final UUID teamMember : team.getAllMembers() ) {
            if (team.isAlive(teamMember)) {
                aliveLines.add(" &f" + PotPvPSI.getInstance().getUuidCache().name(teamMember) + " " + this.getHeartString(team, teamMember));
            } else {
                deadLines.add(" &7&m" + PotPvPSI.getInstance().getUuidCache().name(teamMember));
            }
        }
        final List<String> result=new ArrayList<String>();
        result.addAll(aliveLines);
        result.addAll(deadLines);
        return result;
    }

    private List<String> renderTeamMemberOverviewLines(final MatchTeam team) {
        final List<String> aliveLines=new ArrayList<String>();
        final List<String> deadLines=new ArrayList<String>();
        for ( final UUID teamMember : team.getAllMembers() ) {
            if (team.isAlive(teamMember)) {
                aliveLines.add(" &f" + PotPvPSI.getInstance().getUuidCache().name(teamMember));
            } else {
                deadLines.add(" &7&m" + PotPvPSI.getInstance().getUuidCache().name(teamMember));
            }
        }
        final List<String> result=new ArrayList<String>();
        result.addAll(aliveLines);
        result.addAll(deadLines);
        return result;
    }

    private String getHeartString(final MatchTeam ourTeam, final UUID partnerUuid) {
        if (partnerUuid != null) {
            String healthStr;
            if (ourTeam.isAlive(partnerUuid)) {
                final Player partnerPlayer=Bukkit.getPlayer(partnerUuid);
                final double health=Math.round(partnerPlayer.getHealth()) / 2.0;
                ChatColor healthColor;
                if (health > 8.0) {
                    healthColor=ChatColor.GREEN;
                } else if (health > 6.0) {
                    healthColor=ChatColor.YELLOW;
                } else if (health > 4.0) {
                    healthColor=ChatColor.AQUA;
                } else if (health > 1.0) {
                    healthColor=ChatColor.RED;
                } else {
                    healthColor=ChatColor.DARK_RED;
                }
                healthStr=healthColor.toString() + "(" + health + " \u2764)";
            } else {
                healthStr="&4(RIP)";
            }
            return healthStr;
        }
        return "&4(RIP)";
    }

    public String getArcherMarkScore(final Player player) {
        if (ArcherClass.isMarked(player)) {
            final long diff=ArcherClass.getMarkedPlayers().get(player.getName()) - System.currentTimeMillis();
            if (diff > 0L) {
                return ScoreFunction.getTIME_FANCY().apply(diff / 1000.0f);
            }
        }
        return null;
    }

    public String getBardEffectScore(final Player player) {
        if (BardClass.getLastEffectUsage().containsKey(player.getName()) && BardClass.getLastEffectUsage().get(player.getName()) >= System.currentTimeMillis()) {
            final float diff=(float) (BardClass.getLastEffectUsage().get(player.getName()) - System.currentTimeMillis());
            if (diff > 0.0f) {
                return ScoreFunction.getTIME_SIMPLE().apply((int) (diff / 1000.0f));
            }
        }
        return null;
    }

    public String getBardEnergyScore(final Player player) {
        if (BardClass.getEnergy().containsKey(player.getName())) {
            final float energy=BardClass.getEnergy().get(player.getName());
            if (energy > 0.0f) {
                return String.valueOf(BardClass.getEnergy().get(player.getName()));
            }
        }
        return null;
    }
}
