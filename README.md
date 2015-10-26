[![Build Status](https://travis-ci.org/MageRings/draftomatic.svg?branch=develop)](https://travis-ci.org/MageRings/draftomatic)

# Basic Usage

## Run:
        ./gradlew run

## View the application at:
        http://localhost:8000/magic/

# About

Draftomatic seeks to provide a client-server architecture for swiss pairings.  Ideally this will disassociate the relatively complicated logic of pairings players from the acts of displaying the
pairings or recording results.

#Developing

1. Run `./gradlew eclipse`.  This will fetch the java dependencies
and set up an eclipse project.
2. Run `npm install`.  This will download the node dependnecies.
3. Run `gulp watch` in `magic-app`.  This will generate the frontend
application.  Node that any changes to HTML files must be accompanied
by a trivial change to `magic-app/bower.json`.  This is a known issue.
4.  Before checking in run `./gradlew build` to run test.
