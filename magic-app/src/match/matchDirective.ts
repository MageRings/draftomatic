module Magic.App.Match {

    export class MatchDirective implements ng.IDirective {
        public controller = "matchController";
        public controllerAs = "ctrl";
        public restrict = "E";
        public scope = {
            match: "=",
            tournament: "="
        };
        public templateUrl = "match/matchTemplate.html";
    }

    magic.directive("match", () => new MatchDirective());
}


