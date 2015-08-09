module Magic.App.Tournament {

    export class TournamentController {

        public static $inject = ["$scope", "$http"];
        public tournamentModel: Magic.App.DraftConfig.ITournamentModel;
        public tournament: Swiss;
        constructor($scope: ng.IScope, $http: ng.IHttpService) {
            this.tournamentModel = {
                bestOf: "3",
                format: "Draft",
                numRounds: null,
                players: [],
                set: null
            };
        }
        
        public startTournament() {
            var playerNames = _.pluck(this.tournamentModel.players, "name");
            this.tournament = new Swiss(Number(this.tournamentModel.bestOf), playerNames);
            this.tournament.pairInitial();
            debugger;
        }
        
        public pairNextRound() {
            this.tournament.pairRound();
        }
    }

    magic.controller("tournamentController", TournamentController);
}
