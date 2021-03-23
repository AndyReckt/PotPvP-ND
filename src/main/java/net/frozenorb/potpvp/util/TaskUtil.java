package net.frozenorb.potpvp.util;

import net.frozenorb.potpvp.PotPvPND;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class TaskUtil {
    public TaskUtil() {
    }

    public static void run(Runnable runnable) {
        PotPvPND.getInstance().getServer().getScheduler().runTask(PotPvPND.getInstance(), runnable);
    }

    public static void runTimer(Runnable runnable, long delay, long timer) {
        PotPvPND.getInstance().getServer().getScheduler().runTaskTimer(PotPvPND.getInstance(), runnable, delay, timer);
    }

    public static void runTimer(BukkitRunnable runnable, long delay, long timer) {
        runnable.runTaskTimer(PotPvPND.getInstance(), delay, timer);
    }

    public static void runLater(Runnable runnable, long delay) {
        PotPvPND.getInstance().getServer().getScheduler().runTaskLater(PotPvPND.getInstance(), runnable, delay);
    }

    public static void runSync(Runnable runnable) {
        if (Bukkit.isPrimaryThread())
            runnable.run();
        else
            Bukkit.getScheduler().runTask(PotPvPND.getInstance(), runnable);
    }

    public static void runAsync(Runnable runnable) {
        if (Bukkit.isPrimaryThread())
            Bukkit.getScheduler().runTaskAsynchronously(PotPvPND.getInstance(), runnable);
        else
            runnable.run();
    }
}
