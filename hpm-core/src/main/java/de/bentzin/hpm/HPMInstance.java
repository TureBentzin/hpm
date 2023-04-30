package de.bentzin.hpm;

import de.bentzin.hangar.api.ApiClient;
import de.bentzin.hangar.api.model.Platform;
import org.jetbrains.annotations.NotNull;

/**
 * @author Ture Bentzin
 * 30.04.2023
 */
public class HPMInstance {

    private final @NotNull Platform platform;
    private final @NotNull ApiClient apiClient;

    public HPMInstance(@NotNull String apiKey, @NotNull Platform platform) {
        this.platform = platform;
        apiClient = new ApiClient();

        apiClient.setApiKey(apiKey);

        //authenticate with key



    }

    public @NotNull Platform getPlatform() {
        return platform;
    }

}
