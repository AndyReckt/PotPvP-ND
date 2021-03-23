package net.frozenorb.potpvp.rematch.listener;

import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.duel.DuelHandler;
import net.frozenorb.potpvp.duel.DuelInvite;
import net.frozenorb.potpvp.duel.command.AcceptCommand;
import net.frozenorb.potpvp.duel.command.DuelCommand;
import net.frozenorb.potpvp.kit.listener.KitEditorListener;
import net.frozenorb.potpvp.lobby.menu.DuelSelectorMenu;
import net.frozenorb.potpvp.rematch.RematchData;
import net.frozenorb.potpvp.rematch.RematchHandler;
import net.frozenorb.potpvp.rematch.RematchItems;
import net.frozenorb.potpvp.util.InventoryUtils;
import net.frozenorb.potpvp.util.ItemListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class RematchItemListener extends ItemListener {

    public RematchItemListener(RematchHandler rematchHandler) {
        addHandler(RematchItems.REQUEST_REMATCH_ITEM, player -> {
            RematchData rematchData = rematchHandler.getRematchData(player);

            if (rematchData != null) {
                Player target = Bukkit.getPlayer(rematchData.getTarget());
                DuelCommand.duel(player, target, rematchData.getKitType());

                InventoryUtils.resetInventoryDelayed(player);
                if (!KitEditorListener.isEditingKit(target)) {
                    InventoryUtils.resetInventoryDelayed(target);
                }
            }
        });

        addHandler(RematchItems.SENT_REMATCH_ITEM, p ->
            p.sendMessage(ChatColor.RED + "You have already sent a rematch request."));

        addHandler(RematchItems.ACCEPT_REMATCH_ITEM, player -> {
            RematchData rematchData = rematchHandler.getRematchData(player);

            if (rematchData != null) {
                Player target = Bukkit.getPlayer(rematchData.getTarget());
                AcceptCommand.accept(player, target);
            }
        });

        addHandler(RematchItems.DUEL_SELECTOR, player -> {
            DuelHandler duelHandler = PotPvPND.getInstance().getDuelHandler();
            new DuelSelectorMenu(duelHandler.getInvitesTo(player)).openMenu(player);
        });

        addHandler(RematchItems.ACCEPT_DUEL_ITEM, player -> {
            DuelHandler duelHandler = PotPvPND.getInstance().getDuelHandler();
            DuelInvite duelInvite = duelHandler.getInvitesTo(player).get(0);
            Player sender = Bukkit.getPlayer((UUID) duelInvite.getSender());
            player.performCommand("accept " + sender.getName());
        });
    }

}