package com.gmail.ryderzye.itemcooldown;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

/**
 * Created by marc on 05.01.17.
 */
public class Main extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.getServer().getPluginManager().registerEvents(this, this);

        getLogger().info("ItemCooldown is enabled");
    }

    private boolean isCooldownItem(Material material) {
        return this.getConfig().isSet("items." + material.toString());
    }

    private boolean hasBypassPermission(Player p, Material materialOfItem) {
        String permission = this.getConfig().getString("items." + materialOfItem.toString() + ".bypasspermissions");
        if (permission != null)
            return p.hasPermission(permission);
        return false;
    }

    private Integer getItemCooldownConfig(String itemName) {
        return 20 * this.getConfig().getInt("items." + itemName + ".cooldown");
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
            return;
        }
        if (isCooldownItem(material)) {
            if(player.getCooldown(material) != 0) {
                player.sendMessage("main hand has cooldown, cancelled event");
                event.setCancelled(true);
            } else {
                event.setUseItemInHand(Event.Result.ALLOW);
                sendPacket(player, material, getItemCooldownConfig(material.toString()));
                player.sendMessage("set cooldown");
            }
        }
        player.sendMessage("end");
    }

    private void sendPacket(Player player, Material material, int ticksLeft) {
        if(player == null) return;
        if(material == null) return;

        Runnable task = () -> {
            player.setCooldown(material, ticksLeft);
        };

        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.scheduleSyncDelayedTask(this, task, 1L);
    }
}

