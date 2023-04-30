package de.bentzin.hpm;

import de.bentzin.hangar.api.ApiException;
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
class NestedHPMService implements HPMService {

    private @NotNull HPMInstance hpmInstance;

    protected NestedHPMService(@NotNull HPMInstance hpmInstance) {
        this.hpmInstance = hpmInstance;
    }

    @Override
    public @Unmodifiable @NotNull List<Component> generateInfoPage(@NotNull InfoPanelContext infoPanelContext) {
        return null;
    }

    @Override
    public @Unmodifiable @NotNull List<Project> simpleSearch(@NotNull String q) {
        return null;
    }

    @Override
    public @NotNull Optional<Project> projectInfo(@NotNull String author, @NotNull String slug) {
        try {
            Project project = hpmInstance.projects().getProject(author, slug);
            return Optional.ofNullable(project);
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @NotNull Optional<File> install(@NotNull String author, @NotNull String slug, @NotNull String version) {
        return null;
    }
}
