module Magic.App.Round {

    export class RoundDirective implements ng.IDirective {
        public controller = "roundController";
        public restrict = "E";
        public scope = {
            round: "=",
            tournament: "="
        };
        public templateUrl = "round/roundTemplate.html";
    }

    magic.directive("round", () => new RoundDirective());
}
