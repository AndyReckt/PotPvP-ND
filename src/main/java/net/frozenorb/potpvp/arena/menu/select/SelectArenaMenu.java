package net.frozenorb.potpvp.arena.menu.select;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.arena.ArenaSchematic;
import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.kt.menu.Button;
import net.frozenorb.potpvp.kt.menu.Menu;
import net.frozenorb.potpvp.kt.util.Callback;
import net.frozenorb.potpvp.match.MatchHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;

public class SelectArenaMenu extends Menu {

    private KitType kitType;
    private Callback<ArenaSchematic> mapCallback;
    Set<String> enabledSchematics = Sets.newHashSet();

    public SelectArenaMenu(KitType kitType, Callback<ArenaSchematic> mapCallback, String title) {
        super(ChatColor.BLUE.toString() + ChatColor.BOLD + title);

        this.kitType = kitType;
        this.mapCallback = mapCallback;

        for (ArenaSchematic schematic : PotPvPSI.getInstance().getArenaHandler().getSchematics()) {
            if (MatchHandler.canUseSchematic(this.kitType, schematic) &&
                schematic.isEnabled() &&
                !schematic.isGameMapOnly()) {
                enabledSchematics.add(schematic.getName());
            }
        }
    }

    @Override
    public Map<Integer, Button> getButtons(Player arg0) {
        Map<Integer, Button> buttons = Maps.newHashMap();

        int i = 0;
        for (String mapName : enabledSchematics) {
            buttons.put(i++, new ArenaButton(mapName, mapCallback));
        }

        return buttons;
    }

}
