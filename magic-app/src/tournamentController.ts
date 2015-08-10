module Magic.App.Tournament {

    export class TournamentController {

        public static $inject = ["$scope", "$http"];
        public tournamentModel: Magic.App.DraftConfig.ITournamentModel;
        public tournament: Swiss;
        private http: ng.IHttpService;
        constructor($scope: ng.IScope, $http: ng.IHttpService) {
            this.tournamentModel = {
                format: "Draft",
                numRounds: null,
                players: [],
                set: null
            };
            this.http = $http;
        }
        
        public startTournament() {
            var playerNames = _.pluck(this.tournamentModel.players, "name");
            console.log(this.tournamentModel.players);
            this.http.post<any>("api/tournament/register", this.tournamentModel.players).then((response) => console.log(response));
        }
        
        public pairNextRound() {
            this.tournament.pairRound();
        }
    }

    magic.controller("tournamentController", TournamentController);
}
