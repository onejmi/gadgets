package io.github.scarger.gadgets.gadgetscore;

import io.github.scarger.gadgets.gadgetscore.command.GadgetsCommand;
import io.github.scarger.gadgets.gadgetscore.component.GadgetsMenu;
import io.github.scarger.gadgets.gadgetscore.component.gadget.GadgetManager;
import io.github.scarger.gadgets.gadgetscore.listen.MenuListener;
import io.github.scarger.gadgets.gadgetscore.listen.ProjectileListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class GadgetsCore extends JavaPlugin {

    public final String GADGETS_PERMISSION = "gadget.use";

    private GadgetsMenu menu;
    private GadgetManager manager;

    @Override
    public void onEnable() {
        // Plugin startup login
        menu = new GadgetsMenu(this);
        manager = new GadgetManager(this);

        getCommand("gadget").setExecutor(new GadgetsCommand(this));
        getServer().getPluginManager().registerEvents(new MenuListener(this), this);
        getServer().getPluginManager().registerEvents(new ProjectileListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public GadgetsMenu getMenu() {
        return menu;
    }

    public GadgetManager getManager() {
        return manager;
    }
}
