package net.frozenorb.potpvp.util;

import net.frozenorb.potpvp.PotPvPSI;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class TaskUtil {
    public TaskUtil() {
    }

    public static void run(Runnable runnable) {
        PotPvPSI.getInstance().getServer().getScheduler().runTask(PotPvPSI.getInstance(), runnable);
    }

    public static void runTimer(Runnable runnable, long delay, long timer) {
        PotPvPSI.getInstance().getServer().getScheduler().runTaskTimer(PotPvPSI.getInstance(), runnable, delay, timer);
    }

    public static void runTimer(BukkitRunnable runnable, long delay, long timer) {
        runnable.runTaskTimer(PotPvPSI.getInstance(), delay, timer);
    }

    public static void runLater(Runnable runnable, long delay) {
        PotPvPSI.getInstance().getServer().getScheduler().runTaskLater(PotPvPSI.getInstance(), runnable, delay);
    }

    public static void runSync(Runnable runnable) {
        if (Bukkit.isPrimaryThread())
            runnable.run();
        else
            Bukkit.getScheduler().runTask(PotPvPSI.getInstance(), runnable);
    }

    public static void runAsync(Runnable runnable) {
        if (Bukkit.isPrimaryThread())
            Bukkit.getScheduler().runTaskAsynchronously(PotPvPSI.getInstance(), runnable);
        else
            runnable.run();
    }
}
