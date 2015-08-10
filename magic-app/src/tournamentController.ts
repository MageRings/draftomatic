module Magic.App.Tournament {

    export class TournamentController {

        public static $inject = ["$scope", "$http", "tournament"];
        public tournament: Swiss;
        public tournamentModel: Magic.App.DraftConfig.ITournamentModel;
        public tournamentService: Tournament.ITournamentService;
        constructor($scope: ng.IScope, $http: ng.IHttpService, tournamentService: TournamentService) {
            this.tournamentModel = {
                bestOf: "3",
                format: "Draft",
                numRounds: null,
                players: [],
                set: null
            };
            this.tournamentService = tournamentService;
        }
        
        public startTournament() {
            debugger;
            this.tournamentService.registerTournament(this.tournamentModel.numRounds, this.tournamentModel.format, "formateCode", this.tournamentModel.players).then((data) => {
                return this.tournamentService.getPairings(data.data, 0);
            }).catch((error) => {
                debugger;
            }).then((data) => {
                debugger;
            });
            //this.tournament = new Swiss(Number(this.tournamentModel.bestOf), playerNames);
            //this.tournament.pairInitial();
        }
        
        public pairNextRound() {
            this.tournament.pairRound();
        }
    }

    magic.controller("tournamentController", TournamentController);
}
