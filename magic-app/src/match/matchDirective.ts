module Magic.App.Match {

    export class MatchDirective implements ng.IDirective {
        public controller = "matchController";
        public restrict = "E";
        public scope = {
            round: "=",
            roundNumber: "="
        };
        public templateUrl = "match/matchTemplate.html";
    }

    magic.directive("match", () => new MatchDirective());
}
