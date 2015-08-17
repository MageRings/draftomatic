module Magic.App.Round {

    export class RoundsController {
        private scope: ng.IScope;
        public tournament: any;
        public static $inject = ["$scope", "$stateParams", "$http"];
        public standings: any;
        private tournamentId : string;
        private http : ng.IHttpService;

        constructor($scope: ng.IScope, $stateParams: ng.ui.IStateParamsService, $http: ng.IHttpService) {
            this.tournamentId = $stateParams["id"];
            this.scope = $scope;
            this.http = $http;
            this.http.get<any>("api/tournament/status/" + this.tournamentId).then((response) => {
                console.log(response);
                this.tournament = response.data;
            });
        }

        public pairNextRound() {
            var latestRound = this.tournament.rounds[this.tournament.currentRound-1];
            latestRound.complete = true;
            this.http.put<any>("api/tournament/results/" + this.tournamentId, latestRound.matches).then((response) => {
                console.log(response);
                this.tournament.rounds.push(response.data);
                this.tournament.currentRound = response.data.number;
            });
        }

        public getFinalStandings() {
            var latestRound = this.tournament.rounds[this.tournament.currentRound-1];
            latestRound.complete = true;
            this.http.put<any>("api/tournament/results/" + this.tournamentId, latestRound.matches).then(() => {
                this.http.get<any>("api/tournament/standings/" + this.tournamentId, {round : latestRound.number}).then((response) => {
                    console.log(response);
                    debugger;
                    this.tournament.complete = true;
                    this.standings = response.data;
                });
            });
        }
    }

    magic.controller("roundsController", RoundsController);
}
