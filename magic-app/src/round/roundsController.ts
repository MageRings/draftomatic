module Magic.App.Round {

    export class RoundsController {
        private scope: ng.IScope;
        public tournament: any;
        public seatings: any;
        public static $inject = ["$scope", "$stateParams", "$http"];
        private tournamentId : string;
        private http : ng.IHttpService;
        
        constructor($scope: ng.IScope, $stateParams: ng.ui.IStateParamsService, $http: ng.IHttpService) {
            this.tournamentId = $stateParams["id"];
            this.scope = $scope;
            this.http = $http;
            this.http.get<any>("api/tournament/status/" + this.tournamentId).then((response) => {
                console.log(response);
                this.tournament = response.data.tournamentData;
                this.tournament.complete = response.data.complete;
                this.tournament.currentRound = response.data.currentRound;
                this.tournament.finalStandings = response.data.finalStandings;
                this.http.get<any>("api/tournament/standings/" + this.tournamentId, {round : "1"}).then((response) => {
                    var numPairs = Math.floor(response.data.length/2);
                    this.seatings = new Array(response.data.length);
                    for (var i = 0; i < numPairs; i += 2) {
                        this.seatings[i] = response.data[i].player.name;
                        this.seatings[i+1] = response.data[i + numPairs].player.name;
                        this.seatings[i+ numPairs] = response.data[i + 1].player.name;
                        this.seatings[i+ 1 + numPairs] = response.data[i + 1 + numPairs].player.name;
                    }
                    if (response.data.length%2 != 0) {
                        //there is a bye
                        this.seatings[response.data.length - 1] = response.data[response.data.length-1].player.name;
                    }
                });
            });
        }
        
        public pairNextRound() {
            var latestRound = this.tournament.rounds[this.tournament.currentRound-1];
            latestRound.complete = true;
            this.http.put<any>("api/tournament/results/" + this.tournamentId, latestRound.matches).then((response) => {
                this.tournament.rounds.push(response.data);
                this.tournament.currentRound = response.data.number;
            });
        }
        
        public undoLastRound() {
            this.http.delete<any>("api/tournament/round/" + this.tournamentId).then((response) => {
                this.tournament.complete = false;
                var latestRound = this.tournament.rounds[this.tournament.currentRound-1];
                if (!latestRound.complete) {
                    this.tournament.rounds.pop();
                }
                this.tournament.rounds.pop();
                this.tournament.rounds.push(response.data);
                this.tournament.currentRound = this.tournament.rounds.length;
            });
        }
        
        public getFinalStandings() {
            this.tournament.complete = true;
            var latestRound = this.tournament.rounds[this.tournament.currentRound-1];
            latestRound.complete = true;
            this.http.put<any>("api/tournament/results/" + this.tournamentId, latestRound.matches).then(() => {
                this.http.get<any>("api/tournament/standings/" + this.tournamentId, {round : latestRound.number}).then((response) => {
                    this.tournament.finalStandings = response.data
                });
            });
        }
        
        public downloadTournamentData() {
            window.location.href = "api/tournament/export/" + this.tournamentId;
        }
    }

    magic.controller("roundsController", RoundsController);
}
