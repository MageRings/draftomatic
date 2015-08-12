module Magic.App {
    export var magic = angular.module("magic-app", ["ui.router", "ui.bootstrap", "ui.autocomplete", "ui.bootstrap.typeahead"]);
    export var controllers = angular.module("magic.controllers", []);
    export var directives = angular.module("magic.directives", []);
    export var services = angular.module("magic.services", []);
    function configureStates($stateProvider: ng.ui.IStateProvider, $urlRouterProvider: ng.ui.IUrlRouterProvider) {
        $urlRouterProvider.otherwise("/tournament");

        $stateProvider.state("tournament", {
            url: "/tournament",
            templateUrl: "routes/tournament.html",
            controller: "tournamentController",
            controllerAs: "tournamentController"
        })
        .state("instance", {
            url: "/tournament/{id}",
            templateUrl: "round/rounds.html",
            controller: "roundsController",
            controllerAs: "roundsController"
        });
    }
    magic.config(["$stateProvider", "$urlRouterProvider", configureStates]);
    magic.run(["$state", ($state: ng.ui.IStateService) => $state.go("tournament")]);
}