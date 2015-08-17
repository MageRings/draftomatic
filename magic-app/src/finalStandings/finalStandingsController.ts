module Magic.App.FinalStandings {

    export interface Player {
        id: number;
        name: string;
    }

    export interface FinalStanding {
        finalTiebreaker: string;
        gameWinPercentage: number;
        matchPoints: number;
        opponentMatchWinPercentage: number;
        opponentGameWinPercentage: number;
        player: Player;
    }

    export interface FinalStandingsScope extends ng.IScope {
        standings: FinalStanding[];
    }

    export class FinalStandingsController {
        private scope: FinalStandingsScope;
        public static $inject = ["$scope"];

        constructor($scope: FinalStandingsScope) {
            this.scope = $scope;
            debugger;
        }
    }

    magic.controller("finalStandingsController", FinalStandingsController);
}
