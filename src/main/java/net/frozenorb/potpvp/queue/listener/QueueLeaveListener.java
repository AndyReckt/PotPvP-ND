package net.frozenorb.potpvp.queue.listener;

import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.queue.QueueItems;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class QueueLeaveListener implements Listener {

    public void clickevent(PlayerInteractEvent event) {
        Player player=event.getPlayer();
        if (PotPvPND.getInstance().getLobbyHandler().isInLobby(player) && event.hasItem()) {
            if (event.getItem().isSimilar(QueueItems.LEAVE_SOLO_QUEUE_ITEM) || event.getItem().isSimilar(new ItemStack(Material.INK_SACK, 1, DyeColor.RED.getDyeData()))) {
                PotPvPND.getInstance().getQueueHandler().leaveQueue(player, false);
            }
        }
    }
}

