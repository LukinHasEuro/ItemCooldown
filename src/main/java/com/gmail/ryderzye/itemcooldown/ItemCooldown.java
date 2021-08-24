package com.gmail.ryderzye.itemcooldown;

import com.gmail.ryderzye.itemcooldown.Listener.PlayerInteractionEventListener;
import com.gmail.ryderzye.itemcooldown.Listener.PlayerItemConsumeEventListener;
import org.bukkit.plugin.java.JavaPlugin;

public class ItemCooldown extends JavaPlugin {
    public static ItemCooldown ic;

    public ItemCooldown() {}

    @Override
    public void onEnable() {
        ic = this;
        this.saveDefaultConfig();
        this.getServer().getPluginManager().registerEvents(new PlayerInteractionEventListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerItemConsumeEventListener(), this);

        getLogger().info("ItemCooldown is enabled");
    }

    @Override
    public void onDisable() {
        this.getLogger().info("DamageModifier is disabled");
    }

    public static ItemCooldown get() {
        return ic;
    }
}

