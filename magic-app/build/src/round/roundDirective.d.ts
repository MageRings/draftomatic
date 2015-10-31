declare module Magic.App.Round {
    class RoundDirective implements ng.IDirective {
        restrict: string;
        controller: string;
        controllerAs: string;
        scope: {
            round: string;
        };
        templateUrl: string;
    }
}
