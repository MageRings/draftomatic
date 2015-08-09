module Magic.App.Round {

    export interface IRoundScope extends ng.IScope {
        round: Magic.App.Tournament.Match[];
        tournament: Magic.App.Tournament.Swiss;
        roundController: RoundController;
    }

    export class RoundController {
        private $scope: IRoundScope;
        public draws: number = 0;
        public static $inject = ["$scope", "$http"];
        
        constructor($scope: IRoundScope) {
            this.$scope = $scope;
            $scope.roundController = this;   
        }
        
        public pairNextRound() {
            this.$scope.tournament.pairRound();
        }
    }

    magic.controller("roundController", RoundController);
}
