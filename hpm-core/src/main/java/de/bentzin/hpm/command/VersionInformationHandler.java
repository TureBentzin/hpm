package de.bentzin.hpm.command;

import cloud.commandframework.context.CommandContext;
import cloud.commandframework.execution.CommandExecutionHandler;
import de.bentzin.hangar.api.ApiException;
import de.bentzin.hangar.api.model.Project;
import de.bentzin.hangar.api.model.Version;
import de.bentzin.hpm.HPMInstance;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Objects;

/**
 * @author Ture Bentzin
 * 30.04.2023
 */
public class VersionInformationHandler<C extends Audience> implements CommandExecutionHandler<C> {

    //author, slug, version
    @Override
    public void execute(@NonNull CommandContext<C> commandContext) {
        final String author = commandContext.get("author"), slug = commandContext.get("slug"), version = commandContext.get("version");
        C s = commandContext.getSender();
        Project project = null;
        try {
            project = HPMInstance.instance().projects().getProject(author, slug);
        } catch (ApiException e) {
            s.sendMessage(Component.text("Cant find project!", NamedTextColor.RED));
        }

        Objects.requireNonNull(project, "Project could not be loaded!");

        Version v = null;
        try {
            v = HPMInstance.instance().versions().showVersion(author, slug, version);
        } catch (ApiException e) {
            s.sendMessage(Component.text("Cant find version!", NamedTextColor.RED));
        }

        Objects.requireNonNull(v,"Version could not be loaded!")

        s.sendMessage(Component.text("Information about Project:"));
        s.sendMessage(Component.text("Naming: " + author + " / " + slug));
        String basePath = HPMInstance.instance().getApiClient().getBasePath() + "/" + author + "/" + slug + "/" + version;
        s.sendMessage(Component.text("Information about Version:"));
        s.sendMessage(Component.text("Name: " + v.getName()));
        s.sendMessage(Component.text(v.getDescription()));
        s.sendMessage(Component.text("Channel: " + v.getChannel().getName()));
        s.sendMessage(Component.text("Depends (Debug): " + v.getPlatformDependenciesFormatted()));
        s.sendMessage(Component.text("Link: ").append(Component.text(basePath).clickEvent(ClickEvent.openUrl(basePath))));

    }
}
