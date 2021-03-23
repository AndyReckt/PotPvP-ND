package net.frozenorb.potpvp.kt.nametag

import net.frozenorb.potpvp.PotPvPND
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.metadata.MetadataValue

internal class NametagListener : Listener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        event.player.setMetadata(
            "starkNametag-LoggedIn",
            FixedMetadataValue(PotPvPND.getInstance(), true) as MetadataValue
        )
        PotPvPND.getInstance().nametagEngine.initiatePlayer(event.player)
        PotPvPND.getInstance().nametagEngine.reloadPlayer(event.player)
        PotPvPND.getInstance().nametagEngine.reloadOthersFor(event.player)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        event.player.removeMetadata("starkNametag-LoggedIn", PotPvPND.getInstance())
        PotPvPND.getInstance().nametagEngine.teamMap.remove(event.player.name)
    }
}