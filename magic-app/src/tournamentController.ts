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
    }

    magic.controller("tournamentController", TournamentController);
}
