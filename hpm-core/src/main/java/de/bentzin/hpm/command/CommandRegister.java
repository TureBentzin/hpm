package de.bentzin.hpm.command;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.CommandTree;
import cloud.commandframework.arguments.parser.ParserParameters;
import cloud.commandframework.arguments.parser.StandardParameters;
import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.execution.FilteringCommandSuggestionProcessor;
import cloud.commandframework.extra.confirmation.CommandConfirmationManager;
import cloud.commandframework.meta.CommandMeta;
import cloud.commandframework.minecraft.extras.MinecraftExceptionHandler;
import cloud.commandframework.minecraft.extras.MinecraftHelp;
import cloud.commandframework.paper.PaperCommandManager;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.logging.Logger;

import static net.kyori.adventure.text.Component.text;

/**
 * Like the {@link de.bentzin.tools.register.Registerator} for all HPM commands
 * @author Ture Bentzin
 * 30.04.2023
 */
public class CommandRegister<C extends Audience, M extends CommandManager<C>> {

    private @NotNull M manager;
    private @NotNull MinecraftHelp<C> minecraftHelp;
    private @NotNull CommandConfirmationManager<C> confirmationManager;

    public CommandRegister(@NotNull Logger logger, @NotNull BiFunction<
            Function<CommandTree<C>, CommandExecutionCoordinator<C>>,
            Function<C, C>,
            M
            > functionFunctionCommandManagerBiFunction) {
        //
        // plugin is a function that will provide a command execution coordinator that parses and executes commands
        // asynchronously
        //
        final Function<CommandTree<C>, CommandExecutionCoordinator<C>> executionCoordinatorFunction = AsynchronousCommandExecutionCoordinator.<C>builder().build();
        //
        // However, in many cases it is fine for to run everything synchronously:
        //
        // final Function<CommandTree<C>, CommandExecutionCoordinator<C>> executionCoordinatorFunction =
        //        CommandExecutionCoordinator.simpleCoordinator();
        //
        // plugin function maps the command sender type of our choice to the bukkit command sender.
        // However, in plugin example we use the Bukkit command sender, and so we just need to map it
        // to itself
        //
        final Function<C, C> mapperFunction = Function.identity();


        try {
            this.manager = functionFunctionCommandManagerBiFunction.apply(executionCoordinatorFunction, mapperFunction);
        } catch (final Exception e) {
            logger.severe("Failed to initialize the command this.manager");
            throw new RuntimeException("Failed to initialize the command manager!");
        }


        // Use contains to filter suggestions instead of default startsWith
        this.manager.commandSuggestionProcessor(new FilteringCommandSuggestionProcessor<>(FilteringCommandSuggestionProcessor.Filter.<C>contains(true).andTrimBeforeLastSpace()));

        //
        // Create the Minecraft help menu system
        //
        this.minecraftHelp = new MinecraftHelp<>(
                /* Help Prefix */ "/hpm help", sender -> sender,
                /* Manager */ this.manager);
        /*
            Register Brigadier mappings

        if (this.manager.hasCapability(CloudBukkitCapabilities.BRIGADIER)) {
            this.manager.registerBrigadier();
        }*/
        /*
            Register asynchronous completions
        */
        if (this.manager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            ((PaperCommandManager<C>) this.manager).registerAsynchronousCompletions();
        }
        /*
            Create the confirmation this.manager. Plugin allows us to require certain commands to be
            confirmed before they can be executed in Benefactor
        */
        this.confirmationManager = new CommandConfirmationManager<>(
                /* Timeout */ 30L,
                /* Timeout unit */ TimeUnit.SECONDS,
                /* Action when confirmation is required */ context -> context.getCommandContext().getSender().sendMessage(Component.text("Confirmation required. Confirm using /hpm confirm.").color(NamedTextColor.RED)),
                /* Action when no confirmation is pending */ sender -> sender.sendMessage(Component.text("You don't have any pending commands.").color(NamedTextColor.RED)));
        /*
            Register the confirmation processor. Plugin will enable confirmations for commands that require it
        */
        this.confirmationManager.registerConfirmationProcessor(this.manager);
        /*
            Create the annotation parser. Plugin allows you to define commands using methods annotated with
            @CommandMethod
        */
        final Function<ParserParameters, CommandMeta> commandMetaFunction = p -> CommandMeta.simple()
                // Plugin will allow you to decorate commands with descriptions
                .with(CommandMeta.DESCRIPTION, p.get(StandardParameters.DESCRIPTION, "No description")).build();
        /*
            Override the default exception handlers
        */
        new MinecraftExceptionHandler<C>().withInvalidSyntaxHandler().withInvalidSenderHandler().withNoPermissionHandler().withArgumentParsingHandler().withCommandExecutionHandler().withDecorator(component -> text().append(text("[", NamedTextColor.DARK_GRAY)).append(text("HPM", NamedTextColor.GOLD)).append(text("] ", NamedTextColor.DARK_GRAY)).append(component).build()).apply(this.manager, C -> C);

        HPMCommand.construct(manager);
    }

    public @NotNull M getManager() {
        return manager;
    }
}
