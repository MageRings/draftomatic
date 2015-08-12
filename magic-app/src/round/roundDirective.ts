module Magic.App.Round {

    export class RoundDirective implements ng.IDirective {
        public restrict = "E";
        public scope = {
            round: "=",
        };
        public templateUrl = "round/roundTemplate.html";
    }

    magic.directive("round", () => new RoundDirective());
}
