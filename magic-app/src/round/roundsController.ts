module Magic.App.Round {

    export class RoundsController {
        private scope: ng.IScope;
        public tournament: any;
        public static $inject = ["$scope", "$stateParams", "$http"];
        private tournamentId : string;
        private http : ng.IHttpService;
        
        constructor($scope: ng.IScope, $stateParams: ng.ui.IStateParamsService, $http: ng.IHttpService) {
            console.log($stateParams);
            this.tournamentId = $stateParams.id;
            this.scope = $scope;
            this.http = $http;
            this.http.get<any>("api/tournament/status/" + this.tournamentId).then((response) => {
                console.log(response);
                this.tournament = response.data;
            });
        }
        
        public pairNextRound() {
            var latestRoundMatches = this.tournament.rounds[this.tournament.currentRound-1].matches;
            console.log(latestRoundMatches);
            this.tournament.currentRound += 1;
            this.http.put<any>("api/tournament/results/" + this.tournamentId, latestRoundMatches).then((response) => {
                console.log(response);
                this.tournament.rounds.push(response.data);
            });
        }
    }

    magic.controller("roundsController", RoundsController);
}
