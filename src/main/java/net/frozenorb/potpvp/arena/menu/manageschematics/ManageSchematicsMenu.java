package net.frozenorb.potpvp.arena.menu.manageschematics;

import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.arena.ArenaHandler;
import net.frozenorb.potpvp.arena.ArenaSchematic;
import net.frozenorb.potpvp.command.ManageCommand;
import net.frozenorb.potpvp.kt.menu.Button;
import net.frozenorb.potpvp.kt.menu.Menu;
import net.frozenorb.potpvp.util.menu.MenuBackButton;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public final class ManageSchematicsMenu extends Menu {

    public ManageSchematicsMenu() {
        super("Manage schematics");
        setAutoUpdate(true);
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        ArenaHandler arenaHandler = PotPvPND.getInstance().getArenaHandler();
        Map<Integer, Button> buttons = new HashMap<>();
        int index = 0;

        buttons.put(index++, new MenuBackButton(p -> new ManageCommand.ManageMenu().openMenu(p)));

        for (ArenaSchematic schematic : arenaHandler.getSchematics()) {
            buttons.put(index++, new ManageSchematicButton(schematic));
        }

        return buttons;
    }

}