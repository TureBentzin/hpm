package de.bentzin.hpm;

import de.bentzin.hangar.api.model.Project;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.io.File;
import java.util.List;
import java.util.Optional;

/**
 * @author Ture Bentzin
 * 30.04.2023
 */
public interface HPMService {

    @Unmodifiable @NotNull List<Component> generateInfoPage(@NotNull InfoPanelContext infoPanelContext);

    @Unmodifiable @NotNull List<Project> simpleSearch(@NotNull String q);

    @NotNull Optional<Project> projectInfo(@NotNull String author, @NotNull String slug);

    @NotNull Optional<File> install(@NotNull String author, @NotNull String slug, @NotNull String version);


}
