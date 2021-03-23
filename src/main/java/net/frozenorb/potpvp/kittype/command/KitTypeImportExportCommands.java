package net.frozenorb.potpvp.kittype.command;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.reflect.TypeToken;
import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.kt.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;

public class KitTypeImportExportCommands {
    @Command(names={"kittype export"}, permission="op", async=true)
    public static void executeExport(CommandSender sender) {
        PotPvPND.getInstance().getKitHandler().saveConfigKits();
    }

    @Command(names = { "kittype import" }, permission = "op", async = true)
    public static void executeImport(final CommandSender sender) {
        PotPvPND.getInstance().getKitHandler().loadConfigKits();
        sender.sendMessage(ChatColor.GREEN + "Imported.");
    }
}

