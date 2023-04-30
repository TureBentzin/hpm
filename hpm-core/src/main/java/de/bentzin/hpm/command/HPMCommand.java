package de.bentzin.hpm.command;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.StringArgument;
import de.bentzin.hangar.api.model.Platform;
import de.bentzin.hpm.HPMInstance;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

/**
 * The base class of all HPM commands
 *
 * @author Ture Bentzin
 * 30.04.2023
 */
public class HPMCommand {

    /*
            manager.commandBuilder("hpm", "hangarpm", "hpm-get").permission("hpm")
                .handler(commandContext -> {
                   // commandContext.getSender().sendMessage(); //
                })
                .literal("list")
                .permission("hpm.list")
     */
    public static <C extends Audience> void construct(@NotNull CommandManager<C> manager) {
        manager.command(baseBuilder(manager).handler((context) -> {
            //Base command execution
        }));
        manager.command(baseBuilder(manager).literal("install")
                .argument(StringArgument.single("author"))
                .argument(StringArgument.single("slug"))
                .handler((context) -> {
                    final String author = context.get("author");
                    final String slug = context.get("slug");
                    //install command ex

                    HPMInstance hpmInstance = new HPMInstance("2aa4da39-2aa1-486e-9f7a-6eac30b4fbf9.591b157f-5552-47ba-99f4-4a2731158550"
                            , Platform.PAPER);

                    context.getSender().sendMessage(Component.text(hpmInstance.getHpmService().projectInfo(author, slug).orElseThrow().toString()));
                }));
        manager.command(baseBuilder(manager).literal("info")
                .argument(StringArgument.single("author"))
                .argument(StringArgument.single("slug"))
                .handler(new ProjectInformationHandler<>()));

        manager.command(baseBuilder(manager).literal("info")
                .argument(StringArgument.single("author"))
                .argument(StringArgument.single("slug"))
                .argument(StringArgument.single("version"))
                .handler(new VersionInformationHandler<>()));
    }

    public static <C extends Audience> Command.Builder<C> baseBuilder(@NotNull CommandManager<C> manager) {
        return manager.commandBuilder("hpm", "hangarpm", "hpm-get");
    }
}
