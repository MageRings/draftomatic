package magic;

import com.bazaarvoice.dropwizard.assets.ConfiguredAssetsBundle;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import magic.exceptions.CatchAllExceptionMapper;
import magic.exceptions.IllegalArgumentExceptionMapper;
import magic.exceptions.TournamentNotFoundExceptionMapper;
import magic.resource.DeckResource;
import magic.resource.PlayerResource;
import magic.resource.TournamentResource;

/**
 * Main entry point to the Magic REST service.
 */
public final class MagicApplication extends Application<MagicConfiguration> {

    public static void main(final String[] args) throws Exception {
        new MagicApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap<MagicConfiguration> bootstrap) {
        bootstrap.addBundle(new ConfiguredAssetsBundle("/assets/", "/", "index.html"));
    }

    @Override
    public void run(final MagicConfiguration configuration, final Environment environment) {
        environment.jersey().register(new PlayerResource());
        environment.jersey().register(new DeckResource());
        environment.jersey().register(new TournamentResource());

        //exception mappers
        environment.jersey().register(new CatchAllExceptionMapper());
        environment.jersey().register(new IllegalArgumentExceptionMapper());
        environment.jersey().register(new TournamentNotFoundExceptionMapper());
    }
}
