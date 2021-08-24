package com.gmail.ryderzye.itemcooldown.Listener;

import com.gmail.ryderzye.itemcooldown.ItemCooldown;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;

public class PlayerInteractionEventListener implements Listener {
    public PlayerInteractionEventListener() {
    }

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
    public void playerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (!event.hasItem()) {
            return;
        }

        Player player = event.getPlayer();

        ItemStack item = event.getItem();
        Material material = item.getType();

        if (hasBypassPermission(player, material)) {
            // has cooldown bypass permission, only vanilla cooldown is present
            return;
        }
        if (material.isEdible()) {
            // edible items are handled by PlayerItemConsumeEvent
            return;
        }
        if (isCooldownItem(material)) {
            if(player.getCooldown(material) != 0) {
                event.setCancelled(true);
            } else {
                event.setUseItemInHand(Event.Result.ALLOW);
                sendPacket(player, material, getItemCooldownConfig(material.toString()));
            }
        }
    }

    private void sendPacket(Player player, Material material, int ticksLeft) {
        if(player == null) return;
        if(material == null) return;

        Runnable task = () -> {
            player.setCooldown(material, ticksLeft);
        };

        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.scheduleSyncDelayedTask(ItemCooldown.get(), task, 1L);
    }
}
