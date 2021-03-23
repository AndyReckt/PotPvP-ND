package net.frozenorb.potpvp.hologram;

import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.util.CC;
import net.frozenorb.potpvp.util.TaskUtil;
import org.bukkit.Location;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HologramHandler {

    Map<UUID, Hologram> holograms = new HashMap<>();

    public Hologram hologram;

    public void createKitHologram(Location location, KitType kitType) {
       hologram = HologramsAPI.createHologram(PotPvPND.getInstance(), location);

        TaskUtil.runAsync(() -> {
            if (!location.getChunk().isLoaded())
                location.getChunk().load();
        });

        TaskUtil.runSync(() -> {
            hologram.getVisibilityManager().setVisibleByDefault(true);

            hologram.appendTextLine(CC.translate("&b&lTop 10"));
            hologram.appendTextLine(CC.translate("&b&lLeaderboards"));
            hologram.appendTextLine("");
            hologram.appendTextLine("&a⚉ &a&l" + kitType.getDisplayName() + " &a⚉");
            hologram.appendTextLine("");
            int counter = 1;
            for ( Map.Entry<String, Integer> entry : PotPvPND.getInstance().getEloHandler().topElo(kitType).entrySet()) {
                UUID uuid = PotPvPND.getInstance().getUuidCache().uuid(entry.getKey());
                String division = PotPvPND.getInstance().getDivisionHandler().getDivision(uuid);
                hologram.appendTextLine(CC.translate(CC.WHITE + counter + ". &a" + entry.getKey() + "&f - &b" + entry.getValue() + "&8(" + division+ "&8)"));
                counter++;
            }
        });
        holograms.put(UUID.randomUUID(), hologram);
    }

    public void createGlobalHologram(Location location) {
        hologram = HologramsAPI.createHologram(PotPvPND.getInstance(), location);

        TaskUtil.runAsync(() -> {
            if (!location.getChunk().isLoaded())
                location.getChunk().load();
        });

        TaskUtil.runSync(() -> {
            hologram.getVisibilityManager().setVisibleByDefault(true);

            hologram.appendTextLine(CC.translate("&b&lTop 10"));
            hologram.appendTextLine(CC.translate("&b&lLeaderboards"));
            hologram.appendTextLine("");
            hologram.appendTextLine("&a⚉ &a&lGlobal &a⚉");
            hologram.appendTextLine("");
            int counter = 1;
            for ( Map.Entry<String, Integer> entry : PotPvPND.getInstance().getEloHandler().topElo(null).entrySet()) {
                UUID uuid = PotPvPND.getInstance().getUuidCache().uuid(entry.getKey());
                String division = PotPvPND.getInstance().getDivisionHandler().getDivision(uuid);
                hologram.appendTextLine(CC.translate(CC.WHITE + counter + ". &a" + entry.getKey() + "&f - &b" + entry.getValue() + "&8(" + division+ "&8)"));
                counter++;
            }
        });
        holograms.put(UUID.randomUUID(), hologram);
    }
}
