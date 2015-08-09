module Magic.App.Round {

    export interface IMatchScope extends ng.IScope {
        match: Magic.App.Tournament.Match;
        tournament: Magic.App.Tournament.Swiss;
        matchController: MatchController;
    }

    export class MatchController {
        private $scope: IMatchScope;
        public draws: number = 0;
        public static $inject = ["$scope", "$http"];
        public matchWinner: string;
        constructor($scope: IMatchScope, $http: ng.IHttpService) {
            this.$scope = $scope;
            $scope.matchController = this;
        }

        public setResult(possibleMatchResult: Magic.App.Tournament.MatchResult) {
            this.$scope.match.p1wins = possibleMatchResult.p1wins;
            this.$scope.match.p2wins = possibleMatchResult.p2wins;
            this.$scope.match.draws = this.draws;
        }

        public getPossibleResults() {
            var helper: any;
            switch (this.matchWinner) {
                case "PLAYER_1":
                    helper = (matchResult: Magic.App.Tournament.MatchResult) => {
                        return matchResult.p1wins > matchResult.p2wins;
                    }
                break;
                case "DRAW":
                    helper = (matchResult: Magic.App.Tournament.MatchResult) => {
                        return matchResult.p1wins == matchResult.p2wins;
                    }
                break;
                case "PLAYER_2":
                    helper = (matchResult: Magic.App.Tournament.MatchResult) => {
                        return matchResult.p1wins < matchResult.p2wins;
                    }
                break;
            }   
            return this.$scope.tournament.possibleMatchResults.filter(helper);
        }
    }

    magic.controller("matchController", MatchController);
}
