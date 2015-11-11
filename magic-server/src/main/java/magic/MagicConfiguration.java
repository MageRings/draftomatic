package magic;

import static com.google.common.base.Preconditions.checkNotNull;

import com.bazaarvoice.dropwizard.assets.AssetsBundleConfiguration;
import com.bazaarvoice.dropwizard.assets.AssetsConfiguration;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.Configuration;

/**
 * Server configuration for the Magic REST server.
 */
public final class MagicConfiguration extends Configuration
        implements AssetsBundleConfiguration {

    private final AssetsConfiguration assets;
    private final String databaseType;
    private final String databaseUri;

    @JsonCreator
    public MagicConfiguration(
            @JsonProperty("assets") final AssetsConfiguration assets,
            @JsonProperty("databaseType") final String databaseType,
            @JsonProperty("databaseUri") final String databaseUri) {
        this.assets = checkNotNull(assets);
        this.databaseType = databaseType;
        this.databaseUri = databaseUri;
    }

    @Override
    public AssetsConfiguration getAssetsConfiguration() {
        return assets;
    }

	public String getDatabaseType() {
		return databaseType;
	}

	public String getDatabaseUri() {
		return databaseUri;
	}
}
