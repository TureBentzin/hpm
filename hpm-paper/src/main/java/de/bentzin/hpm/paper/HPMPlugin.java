package de.bentzin.hpm.paper;

import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.paper.PaperCommandManager;
import de.bentzin.hpm.command.CommandRegister;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public final class HPMPlugin extends JavaPlugin {


    // return getProjects(null, null, null, null, null, null, null, null);

    @Override
    public void onEnable() {
        // Plugin startup logic

        CommandRegister<CommandSender, PaperCommandManager<CommandSender>> commandRegister = new CommandRegister<>(this.getLogger(), (coordinator, mapper) -> {
            try {
                PaperCommandManager<CommandSender> manager = new PaperCommandManager<>(this, coordinator, mapper, mapper);
                if (manager.hasCapability(CloudBukkitCapabilities.BRIGADIER)) {
                    manager.registerBrigadier();
                }
                return manager;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

}
