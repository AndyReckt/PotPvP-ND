package net.frozenorb.potpvp.listener;

import org.bukkit.event.Listener;

public final class NightModeListener implements Listener {

    /*public static final int NIGHT_TIME = 18_000;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        SettingHandler settingHandler = PotPvPND.getInstance().getSettingHandler();

        if (settingHandler.getSetting(event.getPlayer(), Setting.NIGHT_MODE)) {
            event.getPlayer().setPlayerTime(NIGHT_TIME, false);
        }
    }

    @EventHandler
    public void onSettingUpdate(SettingUpdateEvent event) {
        if (event.getSetting() != Setting.NIGHT_MODE) {
            return;
        }

        if (event.isEnabled()) {
            event.getPlayer().setPlayerTime(NIGHT_TIME, false);
        } else {
            event.getPlayer().resetPlayerTime();
        }
    }*/

}