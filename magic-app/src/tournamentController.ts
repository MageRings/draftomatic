module Magic.App.Tournament {

    export class TournamentController {

        public static $inject = ["$scope", "$http", "$state"];
        public tournamentModel: Magic.App.DraftConfig.ITournamentModel;
        public state: ng.ui.IStateService;
        private http: ng.IHttpService;
        constructor($scope: ng.IScope, $http: ng.IHttpService, $state: ng.ui.IStateService) {
            this.tournamentModel = {
                format: "Draft",
                numRounds: null,
                players: [],
                set: null
            };
            this.http = $http;
            this.state = $state;
        }
        
        public startTournament() {
            console.log(this.tournamentModel.players);
            console.log("rounds: " + this.tournamentModel.numRounds);
            this.http.post<any>("api/tournament/register",
            {
                "players": this.tournamentModel.players,
                "format": this.tournamentModel.format,
                "code": this.tournamentModel.set,
            },
            {
                "params":{
                    "rounds": this.tournamentModel.numRounds,
                },
            }
            ).then((createTournamentResponse) => {
                var tournamentId = createTournamentResponse.data;
                console.log(tournamentId);
                this.state.go("instance", {id: encodeURI(tournamentId)});
            });
        }

        public shufflePlayers() {
            var i = 0, j = 0, temp = {"id": 1, "name": ""};
            for (i = this.tournamentModel.players.length - 1; i > 0; i -= 1) {
                j = Math.floor(Math.random() * (i + 1));
                temp = this.tournamentModel.players[i];
                this.tournamentModel.players[i] = this.tournamentModel.players[j];
                this.tournamentModel.players[j] = temp;
            }
        }
    }

    magic.controller("tournamentController", TournamentController);
}
