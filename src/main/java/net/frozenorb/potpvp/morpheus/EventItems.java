package net.frozenorb.potpvp.morpheus;

import com.qrakn.morpheus.game.Game;
import com.qrakn.morpheus.game.GameQueue;
import java.util.List;
import net.frozenorb.potpvp.kt.util.ItemBuilder;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public final class EventItems {
    public static ItemStack getEventItem() {
        List<Game> game = GameQueue.INSTANCE.getCurrentGames();
        if (game.size() > 0) {
            return ItemBuilder.of(Material.EMERALD).name(ChatColor.AQUA + "Join An Event" + ChatColor.GRAY + "(Right-Click)").build();
        }
        return null;
    }

    private EventItems() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}

