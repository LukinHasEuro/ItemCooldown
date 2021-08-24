package com.gmail.ryderzye.itemcooldown.Listener;

import com.gmail.ryderzye.itemcooldown.ItemCooldown;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public class PlayerItemConsumeEventListener implements Listener {
    public PlayerItemConsumeEventListener() {}

    private boolean isCooldownItem(Material material) {
        return ItemCooldown.get().getConfig().isSet("items." + material.toString());
    }

    private boolean hasBypassPermission(Player p, Material materialOfItem) {
        String permission = ItemCooldown.get().getConfig().getString("items." + materialOfItem.toString() + ".bypasspermissions");
        if (permission != null)
            return p.hasPermission(permission);
        return false;
    }

    private Integer getItemCooldownConfig(String itemName) {
        return 20 * ItemCooldown.get().getConfig().getInt("items." + itemName + ".cooldown");
    }

    @EventHandler
    public void playerConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        Material material = event.getItem().getType();
        if (hasBypassPermission(player, material)) {
            // has cooldown bypass permission, only vanilla cooldown is present
            return;
        }

        if (isCooldownItem(material)) {
            if(player.getCooldown(material) != 0) {
                event.setCancelled(true);
            } else {
                player.setCooldown(material, getItemCooldownConfig(material.toString()));
            }
        }
    }
}
