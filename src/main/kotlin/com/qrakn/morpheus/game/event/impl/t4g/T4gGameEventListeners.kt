package com.qrakn.morpheus.game.event.impl.t4g

import com.qrakn.morpheus.game.GameQueue
import com.qrakn.morpheus.game.GameState
import com.qrakn.morpheus.game.bukkit.event.PlayerGameInteractionEvent
import com.qrakn.morpheus.game.bukkit.event.PlayerQuitGameEvent
import com.qrakn.morpheus.game.event.impl.brackets.BracketsGameEventLogic
import com.qrakn.morpheus.game.event.impl.lms.LastManStandingGameEvent
import com.qrakn.morpheus.game.event.impl.lms.LastManStandingGameEventLogic
import com.qrakn.morpheus.game.event.impl.sumo.SumoGameEventLogic
import net.frozenorb.potpvp.PotPvPSI
import net.frozenorb.potpvp.kt.util.PlayerUtils.animateDeath
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.*
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.scheduler.BukkitRunnable


class T4gGameEventListeners : Listener {

    @EventHandler
    fun onPlayerDamageEvent(event: EntityDamageEvent) {
        if (event.entity is Player) {
            val player = event.entity as Player
            val game = GameQueue.getCurrentGame(player) ?: return
            val logic = game.logic as? T4gGameEventLogic ?: return
            if (game.event != T4gGameEvent) {
                return
            }
            if (game.players.contains(player) && game.state == GameState.RUNNING) {
                val participant = logic.get(player)
                if (participant != null) {
                    if (!participant.hasDied(player)) {
                        event.damage = 0.0
                        event.isCancelled = false
                        return
                    }
                }
                event.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onEntityDamage(event: EntityDamageByEntityEvent) {
        if (event.damager is Player && event.entity is Player) {
            val damager = event.damager as Player
            val entity = event.entity as Player
            val game = GameQueue.getCurrentGame(damager) ?: return
            val logic = game.logic as? T4gGameEventLogic ?: return
            if (game.event != T4gGameEvent) {
                return
            }
            if (game.players.contains(damager) && game.state == GameState.RUNNING) {
                val participant = logic.get(damager)
                if (participant != null) {
                    T4gGameEventLogic.tagPlayer(damager, entity);
                }
            }
        }

        if (event.cause == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION
                || event.cause == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) {
            event.damage = 0.0;
        }
    }

    @EventHandler
    fun onPlayerDeathEvent(event: PlayerDeathEvent) {
        val game = GameQueue.getCurrentGame(event.entity) ?: return
        val logic = game.logic as? T4gGameEventLogic ?: return
        val participant = logic.get(event.entity) ?: return

        event.drops.clear()

        var location = event.entity.location
        if (location.blockY < 0) {
            location = game.arena.team1Spawn
        }

        if (game.event == T4gGameEvent) {
            for (item in event.drops.toMutableList()) {
                if (item != null && item.type != Material.POTION) {
                    event.drops.remove(item)
                }
            }
        }



        animateDeath(event.entity, false)
        participant.died(event.entity) // todo throw in spectator, play death animation
        object: BukkitRunnable() {
            override fun run() {
                game.spectators.add(event.entity)
                event.entity.spigot().respawn()
                event.entity.teleport(location)
                game.reset(event.entity)
                Bukkit.getPluginManager().callEvent(PlayerGameInteractionEvent(event.entity, game))
//                logic.check()
            }
        }.runTaskLater(PotPvPSI.getInstance(), 2L)


        game.sendMessage("", event.entity.displayName + ChatColor.GRAY + " was eliminated")
    }

    @EventHandler
    fun onPlayerQuitEvent(event: PlayerQuitEvent) {
        val game = GameQueue.getCurrentGame(event.player) ?: return
        val logic = game.logic as? T4gGameEventLogic ?: return
        val participant = logic.get(event.player) ?: return

        participant.died(event.player)
        Bukkit.getPluginManager().callEvent(PlayerQuitGameEvent(event.player, game))
        logic.check()
    }

    @EventHandler
    fun onPlayerQuitGameEvent(event: PlayerQuitGameEvent) {
        val game = GameQueue.getCurrentGame(event.player) ?: return
        val logic = game.logic as? T4gGameEventLogic ?: return
        val participant = logic.get(event.player) ?: return

        if (participant.fighting || participant.starting) {
            participant.died(event.player)
        } else {
            if (participant.players.size == 1 || game.state == GameState.STARTING) {
                logic.participants.remove(participant)
            } else {
                val newPlayers = participant.players.toMutableList()
                newPlayers.remove(event.player)
                participant.players = newPlayers.toTypedArray()
            }
        }
    }

    @EventHandler
    fun entityExplodeEvent(event: EntityExplodeEvent) {
        event.blockList().clear();
    }

    @EventHandler
    fun onFoodLevelChangeEvent(event: FoodLevelChangeEvent) {
        if (event.entity is Player) {
            val player = event.entity as Player
            val game = GameQueue.getCurrentGame(player) ?: return
            val logic = game.logic as? T4gGameEventLogic ?: return

            if (game.event != T4gGameEvent) {
                return
            }

            if (game.players.contains(player)) {
                event.foodLevel = 20
            }
        }
    }
}

