package de.bentzin.hpm.util;

import de.bentzin.hangar.api.ApiException;
import de.bentzin.hangar.api.client.ProjectsApi;
import de.bentzin.hangar.api.model.*;
import de.bentzin.hpm.HPMInstance;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author Ture Bentzin
 * 30.04.2023
 */
public class Util {

    public static @Unmodifiable @NotNull List<Version> getVersions(@NotNull String author, @NotNull String slug, @NotNull Predicate<Version> predicate) {
        try {
            return getVersions(author, slug).stream().filter(predicate).toList();
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
    }

    public static @Unmodifiable @NotNull List<Project> getVProjects(@NotNull Predicate<Project> predicate) {
        try {
            return getProjects(null).stream().filter(predicate).toList();
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
    }

    public static @NotNull List<Project> getProjects(@Nullable String query) throws ApiException {
        ArrayList<Project> projectArrayList = new ArrayList<>();
        long current = 0;
        long count = 0;
        PaginatedResultProject initialResult = getNextProjectsResult(0, query);
        count = initialResult.getPagination().getCount();
        current = initialResult.getPagination().getOffset();
        projectArrayList.addAll(initialResult.getResult());
        while (current < count) {
            PaginatedResultProject nextProjectsResult = getNextProjectsResult(current, query);
            count = nextProjectsResult.getPagination().getCount();
            current = nextProjectsResult.getPagination().getOffset() + nextProjectsResult.getResult().size();
            projectArrayList.addAll(nextProjectsResult.getResult());
            if (projectArrayList.size() > count) {
                break;
            }
        }
        return projectArrayList;
    }

    public static @NotNull List<Version> getVersions(@Nullable String author, @NotNull String slug) throws ApiException {
        ArrayList<Version> versions = new ArrayList<>();
        long current = 0;
        long count = 0;
        PaginatedResultVersion initialResult = getNextVersionResult(0, author, slug);
        count = initialResult.getPagination().getCount();
        current = initialResult.getPagination().getOffset();
        versions.addAll(initialResult.getResult());
        while (current < count) {
            PaginatedResultVersion nextVersionResult = getNextVersionResult(current, author, slug);
            count = nextVersionResult.getPagination().getCount();
            current = nextVersionResult.getPagination().getOffset() + nextVersionResult.getResult().size();
            versions.addAll(nextVersionResult.getResult());
            if (versions.size() > count) {
                break;
            }
        }
        return versions;
    }

    public static @NotNull PaginatedResultProject getNextProjectsResult(long offset, @NotNull String query) throws ApiException {
        RequestPagination requestPagination = new RequestPagination().offset(offset);
        return new ProjectsApi().getProjects(requestPagination.limit(50L), true, null, null, HPMInstance.instance().getPlatform().getValue(), null,
                query, null, null);
    }

    public static @NotNull PaginatedResultVersion getNextVersionResult(long offset, @NotNull String author, @NotNull String slug) throws ApiException {
        RequestPagination requestPagination = new RequestPagination().offset(offset);
        return HPMInstance.instance().versions().listVersions(author, slug, requestPagination.limit(50L), null, HPMInstance.instance().getPlatform().getValue(), null);
    }
}
