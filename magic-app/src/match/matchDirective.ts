module Magic.App.Match {

    export class MatchDirective implements ng.IDirective {
        public restrict = "E";
        public scope = {
            match: "=",
            round: "="
        };
        public templateUrl = "match/matchTemplate.html";
    }

    magic.directive("match", () => new MatchDirective());
}


