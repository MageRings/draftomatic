module Magic.App.Tournament {

    export class TournamentController {

        public static $inject = ["$scope", "$http", "$state"];
        public tournamentModel: Magic.App.DraftConfig.ITournamentModel;
        public tournament: Swiss;
        public state: ng.ui.IState;
        private http: ng.IHttpService;
        constructor($scope: ng.IScope, $http: ng.IHttpService, $state, ng.ui.IState) {
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
            var playerNames = _.pluck(this.tournamentModel.players, "name");
            console.log(this.tournamentModel.players);
            this.http.post<any>("api/tournament/register", this.tournamentModel.players).then((createTournamentResponse) => {
                var tournamentId = createTournamentResponse.data;
                console.log(tournamentId);
                this.state.go("instance", {id: encodeURI(tournamentId)});
            });
        }
    }

    magic.controller("tournamentController", TournamentController);
}
