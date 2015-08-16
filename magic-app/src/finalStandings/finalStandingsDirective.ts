module Magic.App.FinalStandings {

    export class FinalStandingsDirective implements ng.IDirective {
        public controller = "finalStandingsController";
        public controllerAs = "finalStandingsController";
        public restrict = "E";
        public scope = {
        };
        public templateUrl = "finalStandings/finalStandings.html";
    }

    magic.directive("finalStandings", () => new FinalStandingsDirective());
}
