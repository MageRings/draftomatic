module Magic.App.Round {

    export interface IRoundScope extends ng.IScope {
        round: Magic.App.Tournament.Match[];
        tournament: Magic.App.Tournament.Swiss;
        roundController: RoundController;
    }

    export class RoundController {
        private $scope: IRoundScope;
        public draws: number = 0;
        public static $inject = ["$scope", "$stateParams"];
        
        constructor($scope: IRoundScope, $stateParams: ng.ui.IStateParamsService) {
            debugger
            console.log($stateParams);
            debugger
            this.$scope = $scope;
            $scope.roundController = this;   
        }
        
        public pairNextRound() {
            this.$scope.tournament.pairRound();
        }
    }

    magic.controller("roundController", RoundController);
}
