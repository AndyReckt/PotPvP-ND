package net.frozenorb.potpvp.command;

import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.kt.command.Command;
import net.frozenorb.potpvp.util.CC;
import org.bukkit.command.CommandSender;

public class DivisionsReloadCommand {

    @Command(names= "division reload", permission="potpvp.admin")
    public void execute(CommandSender commandSender) {
        PotPvPND.getInstance().getDivisionHandler().loadDivisions();
        commandSender.sendMessage(CC.translate("&aSuccessfully reloaded divisions"));
    }
}
