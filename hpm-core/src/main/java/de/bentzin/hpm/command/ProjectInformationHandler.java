package de.bentzin.hpm.command;

import cloud.commandframework.context.CommandContext;
import cloud.commandframework.execution.CommandExecutionHandler;
import de.bentzin.hangar.api.ApiException;
import de.bentzin.hangar.api.model.Platform;
import de.bentzin.hangar.api.model.Project;
import de.bentzin.hangar.api.model.Version;
import de.bentzin.hpm.HPMInstance;
import de.bentzin.hpm.util.Util;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

/**
 * @author Ture Bentzin
 * 30.04.2023
 */
public class ProjectInformationHandler<C extends Audience> implements CommandExecutionHandler<C> {

    //author, slug, version
    @Override
    public void execute(@NonNull CommandContext<C> commandContext) {
        final String author = commandContext.get("author"), slug = commandContext.get("slug");
        C s = commandContext.getSender();
        Project project = null;
        try {
            project = HPMInstance.instance().projects().getProject(author, slug);
        } catch (ApiException e) {
            s.sendMessage(Component.text("Cant find project!", NamedTextColor.RED));
        }
        Objects.requireNonNull(project, "project could not be loaded!");

        s.sendMessage(Component.text("Information about Project"));
        s.sendMessage(Component.text("Naming: " + author + " / " + slug));

        String basePath = HPMInstance.instance().getApiClient().getBasePath() + "/" + author + "/" + slug;
        s.sendMessage(Component.text("Link: ").append(Component.text(basePath).clickEvent(ClickEvent.openUrl(basePath))));

        s.sendMessage(Component.text(project.getDescription()));
        s.sendMessage(Component.text("Visibility: " + project.getVisibility().getValue()));
        s.sendMessage(Component.text("Category: " + project.getCategory().getValue()));
        s.sendMessage(Component.text("Stars: " + project.getStats().getStars()));
        s.sendMessage(Component.text("Downloads: " + project.getStats().getDownloads()));
        s.sendMessage(Component.text("Downloads (Recent): " + project.getStats().getRecentDownloads()));
        s.sendMessage(Component.text("Watchers: " + project.getStats().getWatchers()));
        s.sendMessage(Component.text("Views: " + project.getStats().getViews()));

        try {
            List<Version> versions = Util.getVersions(author, slug);
            printVersionOverview(versions, commandContext.getSender(), HPMInstance.instance().getPlatform());
        } catch (ApiException e) {
            s.sendMessage(Component.text("Cant find information about versions!", NamedTextColor.RED));
        }
    }

    protected void printVersionOverview(@NotNull List<Version> versions, @NotNull Audience audience, @NotNull Platform platform) {
        versions = versions.
                stream()
                .filter(version ->
                        Objects.requireNonNull(version.getPlatformDependenciesFormatted())
                                .containsKey(platform.getValue()))
                .toList();

        Stream<String> distinctVersionRanges = versions.stream()
                .map(version ->
                        Objects.requireNonNull(version.getPlatformDependenciesFormatted()).get(platform.getValue()))
                .distinct();

        //lowest
        OptionalDouble lowest = distinctVersionRanges.flatMapToDouble(s -> DoubleStream.of(Double.valueOf(s.substring(0, s.indexOf("-")))))
                .distinct()
                .sorted()
                .findFirst();

        OptionalDouble highest = distinctVersionRanges.flatMapToDouble(s -> DoubleStream.of(Double.valueOf(s.substring(s.indexOf("-" + 1)))))
                .distinct()
                .sorted()
                .findFirst();

        audience.sendMessage(Component.text("Versions available for a range of "+ lowest + " to " + highest + "!"));
    }


}
