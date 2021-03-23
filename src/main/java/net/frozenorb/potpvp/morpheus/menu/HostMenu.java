package net.frozenorb.potpvp.morpheus.menu;

import com.qrakn.morpheus.game.event.GameEvent;
import java.util.HashMap;
import java.util.Map;
import net.frozenorb.potpvp.kt.menu.Button;
import net.frozenorb.potpvp.kt.menu.Menu;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HostMenu
extends Menu {
    public HostMenu() {
        super(ChatColor.GRAY + "Host an event");
    }

    @NotNull
    @Override
    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> toReturn = new HashMap<>();
        for (GameEvent event : GameEvent.getEvents()) {
            toReturn.put(toReturn.size(), new HostEventButton(event));
        }
        return toReturn;
    }
}

