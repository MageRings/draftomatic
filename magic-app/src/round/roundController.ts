module Magic.App.Round {

    export class RoundController {
        private scope: ng.IScope;
        public static $inject = ["$scope"];
        
        constructor($scope: ng.IScope) {
            this.scope = $scope;
        }
        
        public completeRoundSuffix(match: any) {
            if (match.result.p1Wins > 0 || match.result.p2Wins > 0 || match.result.draws > 0) {
                var result = " - ";
                if (match.result.p1Wins > match.result.p2Wins) {
                    return result + match.pairing.player1.name + " wins";
                }
                if (match.result.p2Wins > match.result.p1Wins) {
                    return result + match.pairing.player2.name + " wins";
                }
                return result + " match draw";
            }
            return "";
        }
    }

    magic.controller("roundController", RoundController);
}
