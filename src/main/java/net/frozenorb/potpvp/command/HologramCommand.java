package net.frozenorb.potpvp.command;

import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.kt.command.Command;
import net.frozenorb.potpvp.kt.command.data.parameter.Param;
import net.frozenorb.potpvp.util.CC;
import org.bukkit.entity.Player;

public class HologramCommand {

    @Command(names = {"hologram create"}, permission = "potpvp.admin")
    public void execute(Player player, @Param(name = "type: (global/kittype)") String type) {
        if (type.equalsIgnoreCase("global")) {
            PotPvPND.getInstance().getHologramHandler().createGlobalHologram(player.getLocation());
            player.sendMessage(CC.GREEN + "Hologram Created Successfully!");
        } else {
            KitType kitType = KitType.byId(type.toUpperCase());
            if (kitType == null) {
                player.sendMessage(CC.RED + "That kit does not exist");
                return;
            }
            PotPvPND.getInstance().getHologramHandler().createKitHologram(player.getLocation(), kitType);
            player.sendMessage(CC.GREEN + "Hologram Created Successfully!");
            return;
        }
        player.sendMessage(CC.translate("&cInvalid Type!"));

    }

}
