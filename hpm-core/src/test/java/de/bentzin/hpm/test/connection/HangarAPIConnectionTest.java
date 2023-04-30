package de.bentzin.hpm.test.connection;

import de.bentzin.hangar.api.ApiClient;
import de.bentzin.hangar.api.JSON;
import de.bentzin.hangar.api.client.ProjectsApi;
import de.bentzin.hangar.api.client.VersionsApi;
import de.bentzin.hangar.api.model.PaginatedResultProject;
import de.bentzin.hangar.api.model.RequestPagination;
import de.bentzin.hangar.api.model.Version;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Ture Bentzin
 * 30.04.2023
 */
public class HangarAPIConnectionTest {

    @Test
    public void versionsAPI() {
        VersionsApi versionsApi = new VersionsApi();
        assertNotNull(versionsApi);
        ApiClient apiClient = versionsApi.getApiClient();
        assertDoesNotThrow(() -> printClient(apiClient));

        //now test the api
        assertDoesNotThrow(() -> {
            Version version = versionsApi.showVersion("TureBentzin", "Core", "1.0");//demo just to check for exception
        });
    }

    @Test
    public void projectsAPI() {
        ProjectsApi projectsApi = new ProjectsApi();
        assertNotNull(projectsApi);

        assertDoesNotThrow(() -> printClient(projectsApi.getApiClient()));

        assertDoesNotThrow(() -> {
            PaginatedResultProject name = projectsApi.getProjects(new RequestPagination().limit(10L), true, "slug", null, null, null, null, null, null);

        });
    }


    private void printClient(@NotNull ApiClient apiClient) {
        JSON json = apiClient.getJSON();
        System.out.println("API-Client = " + json.getGson().toString());
    }
}
