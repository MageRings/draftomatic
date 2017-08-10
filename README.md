[![Build Status](https://travis-ci.org/MageRings/draftomatic.svg?branch=develop)](https://travis-ci.org/MageRings/draftomatic)

# Basic Usage

You can try out a live instance of draftomatic [here](https://draftomatic.herokuapp.com/magic/#/tournament).

## Run:
        ./gradlew run

## View the application at:
        http://localhost:8000/magic/

# About

Draftomatic seeks to provide a client-server architecture for swiss pairings.  Ideally this will disassociate the relatively complicated logic of pairings players from the acts of displaying the
pairings or recording results.

## License

Draftomatic is licensed under the GNU Affero GPL v3.0. See [this link](http://blog.mongodb.org/post/103832439/the-agpl) for more information.  This license was chosen because draftomatic is intended to act as a
standalone application rather than as a library.  If you think that some portion of the project would be useful to you
under a more permissive license please let us know.

# Developing

1. Run `./gradlew eclipse`.  This will fetch the java dependencies
and set up an eclipse project.  Note that if you want to launch the
application from eclipse you need to manually configure your launch target
to point to the root of this repository.
2. Run `npm install`.  This will download the node dependnecies.
3. Run `gulp watch` in `magic-app`.  This will generate the frontend
application.  Note that any changes to HTML files must be accompanied
by a trivial change to `magic-app/bower.json`.  This is a known issue.
4.  Before submitting a pull request, use `./gradlew build` to run tests.
