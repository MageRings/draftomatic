package magic;

import com.bazaarvoice.dropwizard.assets.ConfiguredAssetsBundle;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import magic.data.database.Database;
import magic.data.database.FileSystemDB;
import magic.data.database.HerokuDB;
import magic.data.database.NoopDB;
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
        Database db;
        System.out.println(configuration.getDatabaseType());
        System.out.println(configuration.getDatabaseUri());
    	switch (configuration.getDatabaseType()) {
    	case "heroku":
    		try {
    			db = new HerokuDB(configuration.getDatabaseUri());
    			break;
    		} catch (Exception e) {
    			throw new RuntimeException(e);
    		}
    	case "noop": 
    		db = NoopDB.NOOPDB;
    		break;
    	default: 
    		db = new FileSystemDB();
    	}

        environment.jersey().register(new PlayerResource(db));
        environment.jersey().register(new DeckResource());
        environment.jersey().register(new TournamentResource(db));

        // exception mappers
        environment.jersey().register(new CatchAllExceptionMapper());
        environment.jersey().register(new IllegalArgumentExceptionMapper());
        environment.jersey().register(new TournamentNotFoundExceptionMapper());
    }
}
