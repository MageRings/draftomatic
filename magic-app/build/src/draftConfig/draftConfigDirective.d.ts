declare module Magic.App.DraftConfig {
    class DraftConfigDirective implements ng.IDirective {
        controller: string;
        controllerAs: string;
        restrict: string;
        scope: {
            tournamentModel: string;
            allPlayers: string;
        };
        templateUrl: string;
    }
}
