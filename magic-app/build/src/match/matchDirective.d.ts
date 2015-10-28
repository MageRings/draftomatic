declare module Magic.App.Match {
    class MatchDirective implements ng.IDirective {
        restrict: string;
        scope: {
            match: string;
            round: string;
        };
        templateUrl: string;
    }
}
