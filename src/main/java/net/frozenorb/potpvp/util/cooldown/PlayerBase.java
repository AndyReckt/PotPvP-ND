package net.frozenorb.potpvp.util.cooldown;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;

@AllArgsConstructor
@Getter
public class PlayerBase extends CustomEvent {

    private final Player player;
}
