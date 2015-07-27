package magic;

import static com.google.common.base.Preconditions.checkNotNull;

import com.bazaarvoice.dropwizard.assets.AssetsBundleConfiguration;
import com.bazaarvoice.dropwizard.assets.AssetsConfiguration;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Optional;

import io.dropwizard.Configuration;

/**
 * Server configuration for the Magic REST server.
 */
public final class MagicConfiguration extends Configuration
        implements AssetsBundleConfiguration {

    private static final String DEFAULT_VALUE = "Sarah Kerrigan";

    private final AssetsConfiguration assets;
    private final String defaultName;

    @JsonCreator
    public MagicConfiguration(
            @JsonProperty("assets") final AssetsConfiguration assets,
            @JsonProperty("defaultName") final Optional<String> defaultName) {
        checkNotNull(assets);
        checkNotNull(defaultName);

        this.assets = assets;
        this.defaultName = defaultName.or(DEFAULT_VALUE);
    }

    public String getDefaultName() {
        return this.defaultName;
    }

    @Override
    public AssetsConfiguration getAssetsConfiguration() {
        return assets;
    }
}
