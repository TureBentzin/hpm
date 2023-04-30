package de.bentzin.hpm;

import de.bentzin.hangar.api.ApiClient;
import de.bentzin.hangar.api.ApiException;
import de.bentzin.hangar.api.client.AuthenticationApi;
import de.bentzin.hangar.api.client.ProjectsApi;
import de.bentzin.hangar.api.client.VersionsApi;
import de.bentzin.hangar.api.model.ApiSession;
import de.bentzin.hangar.api.model.Platform;
import de.bentzin.tools.misc.SubscribableType;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Ture Bentzin
 * 30.04.2023
 */
public class HPMInstance {

    private static @NotNull HPMInstance hpmInstance = null;

    ;
    private final @NotNull Platform platform;
    private final @NotNull ApiClient apiClient;
    private final @NotNull Logger logger;
    private final @NotNull SubscribableType<ApiSession> session = new SubscribableType<>();
    private final @NotNull NestedHPMService hpmService;
    private final double version;

    public HPMInstance(@NotNull Platform platform, double version) {
        this(null, platform, version);
    }

    public HPMInstance(@Nullable String apiKey, @NotNull Platform platform, double version) {
        this.version = version;
        hpmInstance = this;
        this.platform = platform;
        logger = Logger.getLogger("hpm-instance");
        apiClient = new ApiClient();
        apiClient.setBasePath("https://hangar.papermc.io"); //TODO Config
        //authenticate with key
        if (apiKey != null)
            try {
                ApiSession authenticate = auth().authenticate(apiKey);
                session.set(authenticate);
                String s = DurationFormatUtils.formatDurationWords(authenticate.getExpiresIn(), true, true);
                logger.log(Level.INFO, "Successfully connected and authenticated with Hangar! Expires in: " + s);
            } catch (ApiException | NullPointerException e) {
                String substring = apiKey.substring(apiKey.length() - 5, apiKey.length() - 1);
                logger.log(Level.SEVERE, "Could not authenticate with apiKey ending with: "
                        + String.valueOf('*').repeat(Math.max(0, (apiKey.length() - 4))) + substring);
                throw new IllegalStateException("Authentication wasn't completed! This is not a Bug or Crash!", e);
            }


        //POST LOGON
        hpmService = new NestedHPMService(this);

    }

    public static @NotNull HPMInstance instance() {
        return hpmInstance;
    }

    public static <R> @NotNull R init(@NotNull HPMInstance instance, @NotNull Function<ApiClient, R> initiator) {
        return initiator.apply(instance.apiClient);
    }

    /**
     * Will authenticate the Hangar if the current session is expired
     *
     * @return the new or old {@link ApiSession}
     */
    public @Nullable ApiSession reAuthenticate() {
        return reAuthenticate(false);
    }

    /**
     * Will authenticate the Hangar if the current session is expired
     *
     * @return the new or old {@link ApiSession}
     */
    public @Nullable ApiSession reAuthenticate(boolean forceReAuth) {
        if(!session.isPresent()) {
            // No Auth
            return null;
        }

        if (forceReAuth)
            try {
                getLogger().log(Level.INFO, "Trying to authenticate with Hangar..."); //DEBUG
                return auth().authenticate(session.get().getToken());
            } catch (ApiException e) {
                throw new RuntimeException(e);
            }

        getSessionType().ifPresent(apiSession -> {
            if (apiSession.getExpiresIn() > System.currentTimeMillis() + 300000)
                reAuthenticate(true);
        });
        throw new RuntimeException("Fatal failure while reAuthenticating! Please contact the developer for further support!");
    }

    public @NotNull Platform getPlatform() {
        return platform;
    }

    public @NotNull ApiClient getApiClient() {
        return apiClient;
    }

    public @NotNull AuthenticationApi auth() {
        return init(this, AuthenticationApi::new);
    }

    public @NotNull ProjectsApi projects() {
        return init(this, ProjectsApi::new);
    }

    public @NotNull VersionsApi versions() {
        return init(this, VersionsApi::new);
    }

    public @NotNull Logger getLogger() {
        return logger;
    }

    public @NotNull SubscribableType<ApiSession> getSessionType() {
        return session;
    }

    @ApiStatus.Experimental
    public @Nullable ApiSession getSession() {
        return session.getOr(null);
    }

    public @NotNull HPMService getHpmService() {
        return hpmService;
    }

    public double getVersion() {
        return version;
    }
}
